package chess.domain.dao;

import chess.domain.entity.Chess;

import java.util.Optional;

public interface ChessDao {
    void save(final Chess chess);

    Optional<Chess> findByName(final String name);

    void update(final Chess chess);

    void deleteByName(final String name);
}
