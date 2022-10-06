package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.board.BoardComment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoardComment.boardComment;

@Repository
@AllArgsConstructor
public class BoardCommentRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardComment boardComment) {
        em.persist(boardComment);
    }

    public void saveAndFlush(BoardComment boardComment) {
        save(boardComment);
        em.flush();
    }

    public List<BoardComment> findByBoardId(Long boardId) {
        return queryFactory
                .selectFrom(boardComment)
                .where(boardComment.board.id.eq(boardId))
                .orderBy(boardComment.createdTime.asc())
                .fetch();
    }

    public void deleteById(Long boardCommentId) {
        queryFactory
                .delete(boardComment)
                .where(boardComment.id.eq(boardCommentId))
                .execute();
    }

}
