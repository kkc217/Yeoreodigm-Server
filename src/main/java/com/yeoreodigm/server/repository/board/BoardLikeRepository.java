package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardLike;
import com.yeoreodigm.server.domain.board.QBoardLike;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoardLike.boardLike;

@Repository
@AllArgsConstructor
public class BoardLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardLike boardLike) {
        em.persist(boardLike);
    }

    public void saveAndFlush(BoardLike boardLike) {
        save(boardLike);
        em.flush();
    }

    public List<Board> findBoardByMemberIdPaging(Long memberId, int page, int limit) {
        return queryFactory
                .selectFrom(boardLike.board)
                .where(boardLike.member.id.eq(memberId))
                .orderBy(boardLike.id.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public Long countByBoardId(Long boardId) {
        return queryFactory
                .select(boardLike.count())
                .from(boardLike)
                .where(boardLike.board.id.eq(boardId))
                .fetchOne();
    }

    public BoardLike findByBoardIdAndMemberId(Long boardId, Long memberId) {
        return queryFactory
                .selectFrom(boardLike)
                .where(boardLike.board.id.eq(boardId), boardLike.member.id.eq(memberId))
                .fetchOne();
    }

    public void deleteById(Long boardLikeId) {
        queryFactory
                .delete(boardLike)
                .where(boardLike.id.eq(boardLikeId))
                .execute();
    }

}
