package com.yeoreodigm.server.repository.board;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoard.board;
import static com.yeoreodigm.server.domain.board.QBoardLike.boardLike;

@Repository
@AllArgsConstructor
public class BoardRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Board board) {
        em.persist(board);
    }

    public void saveAndFlush(Board board) {
        save(board);
        em.flush();
    }

    public Board findById(Long boardId) {
        try {
            return queryFactory
                    .selectFrom(board)
                    .where(board.id.eq(boardId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행 피드가 둘 이상입니다.");
        }
    }

    public List<Board> findByMember(Member member) {
        return queryFactory
                .selectFrom(board)
                .where(board.member.id.eq(member.getId()))
                .fetch();
    }

    public List<Board> findPublicByMember(Member member) {
        return queryFactory
                .selectFrom(board)
                .where(board.publicShare.eq(true), board.member.id.eq(member.getId()))
                .fetch();
    }

    public List<Board> findPublicPaging(int page, int limit) {
        return queryFactory
                .selectFrom(board)
                .where(board.publicShare.eq(true))
                .orderBy(board.createdTime.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Board> findPublicOrderByLikeAsc(int page, int limit) {
        return queryFactory
                .selectFrom(board)
                .leftJoin(boardLike)
                .on(boardLike.board.id.eq(board.id))
                .where(board.publicShare.eq(true))
                .groupBy(board.id, boardLike.board.id)
                .orderBy(board.count().asc(), boardLike.board.id.asc().nullsFirst())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Board> findPublicOrderByLikeDesc(int page, int limit) {
        return queryFactory
                .selectFrom(board)
                .leftJoin(boardLike)
                .on(boardLike.board.id.eq(board.id))
                .where(board.publicShare.eq(true))
                .groupBy(board.id, boardLike.board.id)
                .orderBy(board.count().desc(), boardLike.board.id.desc().nullsLast())
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
