package com.yeoreodigm.server.repository.board;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardComment;
import com.yeoreodigm.server.exception.BadRequestException;
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

    public BoardComment findById(Long boardCommentId) {
        try {
            return queryFactory
                    .selectFrom(boardComment)
                    .where(boardComment.id.eq(boardCommentId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행 피드 댓글이 둘 이상입니다.");
        }
    }

    public List<BoardComment> findByBoardId(Long boardId) {
        return queryFactory
                .selectFrom(boardComment)
                .where(boardComment.board.id.eq(boardId))
                .orderBy(boardComment.createdTime.asc())
                .fetch();
    }

    public Long countByBoard(Board board) {
        return queryFactory
                .select(boardComment.count())
                .from(boardComment)
                .where(boardComment.board.id.eq(board.getId()))
                .fetchOne();
    }

    public void deleteById(Long boardCommentId) {
        queryFactory
                .delete(boardComment)
                .where(boardComment.id.eq(boardCommentId))
                .execute();
    }

}
