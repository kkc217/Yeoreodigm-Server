package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.board.BoardCommentLike;
import com.yeoreodigm.server.domain.board.QBoardCommentLike;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.board.QBoardCommentLike.boardCommentLike;

@Repository
@AllArgsConstructor
public class BoardCommentLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardCommentLike boardCommentLike) {
        em.persist(boardCommentLike);
    }

    public void saveAndFlush(BoardCommentLike boardCommentLike) {
        save(boardCommentLike);
        em.flush();
    }

    public Long countByBoardCommentId(Long boardCommentId) {
        return queryFactory
                .select(boardCommentLike.count())
                .from(boardCommentLike)
                .where(boardCommentLike.boardComment.id.eq(boardCommentId))
                .fetchOne();
    }

    public BoardCommentLike findByBoardCommentIdAndMemberId(Long boardCommentId, Long memberId) {
        return queryFactory
                .selectFrom(boardCommentLike)
                .where(boardCommentLike.boardComment.id.eq(boardCommentId), boardCommentLike.member.id.eq(memberId))
                .fetchOne();
    }

    public void deleteById(Long boardCommentLikeId) {
        queryFactory
                .delete(boardCommentLike)
                .where(boardCommentLike.id.eq(boardCommentLikeId))
                .execute();
    }

}
