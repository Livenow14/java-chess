package chess;

import chess.controller.ChessWebController;
import chess.domain.dao.CustomConnectionPool;
import chess.domain.dao.DBChessDao;
import chess.domain.dao.DBMovementDao;
import chess.service.ChessService;

public class WebUIChessApplication {
    public static void main(String[] args) {
        ChessService chessService = new ChessService(new DBChessDao(CustomConnectionPool.create()), new DBMovementDao(CustomConnectionPool.create()));
        ChessWebController chessWebController = new ChessWebController(chessService);
        chessWebController.run();
    }
}
