package chess.domain.board;

import chess.domain.piece.Piece;
import chess.domain.position.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChessBoardFactoryTest {

    @DisplayName("Board 정상 초기화 테스트")
    @Test
    void initialize() {
        Map<Position, Piece> board = ChessBoardFactory.initializeBoard();

        Piece piece1 = board.get(Position.valueOf("1", "a"));
        Piece piece2 = board.get(Position.valueOf("2", "a"));
        Piece piece3 = board.get(Position.valueOf("3", "a"));

        assertThat(piece1).isEqualTo(null);
        assertThat(piece2).isEqualTo(null);
        assertThat(piece3).isEqualTo(null);
    }

    @DisplayName("유효한(1~8, a~h)의 범위가 아니면 에러가 발생한다.")
    @Test
    void initializeException() {
        Map<Position, Piece> board = ChessBoardFactory.initializeBoard();

        assertThatThrownBy(() -> board.get(Position.valueOf("0", "a")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.get(Position.valueOf("1", "j")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.get(Position.valueOf("1", "-a")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
