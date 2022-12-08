package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardPlace;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoardPlace.boardPlace;

@Repository
@AllArgsConstructor
public class BoardPlaceRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardPlace boardPlace) {
        em.persist(boardPlace);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(BoardPlace boardPlace) {
        save(boardPlace);
        flush();
    }

    public List<BoardPlace> findByBoardId(Long boardId) {
        return queryFactory
                .selectFrom(boardPlace)
                .where(boardPlace.board.id.eq(boardId))
                .fetch();
    }

    public List<BoardPlace> findByPlaceId(Long placeId) {
        return queryFactory
                .selectFrom(boardPlace)
                .where(boardPlace.place.id.eq(placeId))
                .fetch();
    }

    public List<Places> findPlaceByBoardId(Long boardId) {
        return queryFactory
                .select(boardPlace.place)
                .from(boardPlace)
                .where(boardPlace.board.id.eq(boardId))
                .fetch();
    }

    public List<Board> findBoardByPlaceId(Long placeId) {
        return queryFactory
                .select(boardPlace.board)
                .from(boardPlace)
                .where(boardPlace.place.id.eq(placeId))
                .fetch();
    }

    public void deleteById(Long boardPlaceId) {
        queryFactory
                .delete(boardPlace)
                .where(boardPlace.id.eq(boardPlaceId))
                .execute();
    }

}
