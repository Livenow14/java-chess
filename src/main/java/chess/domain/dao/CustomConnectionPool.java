package chess.domain.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomConnectionPool implements ConnectionPool {
    private static final String DEFAULT_SERVER = "jdbc:mysql://localhost:13306"; // 서버 주소
    private static final String DEFAULT_DATABASE = "woowa_chess"; // DATABASE 이름
    private static final String DEFAULT_OPTION = "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8"; // DATABASE 옵션
    private static final String DEFAULT_URL = DEFAULT_SERVER + "/" + DEFAULT_DATABASE + DEFAULT_OPTION; // DATABASE 옵션
    private static final String DEFAULT_USER = "root"; // 서버 아이디
    private static final String DEFAULT_PASSWORD = "root"; // 서버 비밀번호
    private static final int INITIAL_POOL_SIZE = 15;

    private String url;
    private String user; // 서버 아이디
    private String password; // 서버 비밀번호
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();

    private CustomConnectionPool(final String url, final String user, final String password, final List<Connection> connectionPool) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.connectionPool = connectionPool;
    }

    public static CustomConnectionPool create(final String url, final String user, final String password) {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new CustomConnectionPool(url, user, password, pool);
    }

    public static CustomConnectionPool create() {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD));
        }
        return new CustomConnectionPool(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD, pool);
    }

    private static Connection createConnection(final String url, final String user, final String password) {
        // 드라이버 연결
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("연결 오류:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Connection getConnection() {
        if (connectionPool.isEmpty()) {
            validateSize();
        }
        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        try {
            if (!connection.isValid(1_000)) {
                connection = createConnection(url, user, password);
            }

        } catch (SQLException e) {
            System.err.println("시간 오류" + e.getMessage());
            e.printStackTrace();
        }

        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(final Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    public void shutdown() {
        usedConnections.forEach(this::releaseConnection);
        connectionPool.forEach(c -> {
            try {
                c.close();
            } catch (SQLException e) {
                System.err.println("커넥션 해제 오류" + e.getMessage());
                e.printStackTrace();
            }
        });
        connectionPool.clear();
    }

    private void validateSize() {
        if (usedConnections.size() < INITIAL_POOL_SIZE) {
            connectionPool.add(createConnection(url, user, password));
            return;
        }
        throw new RuntimeException("Pool 사이즈를 넘어섰습니다.");
    }
}
