package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardTravelNote;
import com.yeoreodigm.server.domain.board.QBoardTravelNote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoardTravelNote.boardTravelNote;

@Repository
@AllArgsConstructor
public class BoardTravelNoteRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardTravelNote boardTravelNote) {
        em.persist(boardTravelNote);
    }

    public void saveAndFlush(BoardTravelNote boardTravelNote) {
        save(boardTravelNote);
        em.flush();
    }

    public List<BoardTravelNote> findByBoardId(Long boardId) {
        return queryFactory
                .selectFrom(boardTravelNote)
                .where(boardTravelNote.board.id.eq(boardId))
                .fetch();
    }

    public List<BoardTravelNote> findByTravelNoteId(Long travelNoteId) {
        return queryFactory
                .selectFrom(boardTravelNote)
                .where(boardTravelNote.travelNote.id.eq(travelNoteId))
                .fetch();
    }

    public List<TravelNote> findTravelNoteByBoardId(Long boardId) {
        return queryFactory
                .select(boardTravelNote.travelNote)
                .from(boardTravelNote)
                .where(boardTravelNote.board.id.eq(boardId))
                .fetch();
    }

    public List<Board> findBoardByTravelNoteId(Long travelNoteId) {
        return queryFactory
                .select(boardTravelNote.board)
                .from(boardTravelNote)
                .where(boardTravelNote.travelNote.id.eq(travelNoteId))
                .fetch();
    }

    public void deleteById(Long boardTravelNoteId) {
        queryFactory
                .delete(boardTravelNote)
                .where(boardTravelNote.id.eq(boardTravelNoteId))
                .execute();
    }

}
