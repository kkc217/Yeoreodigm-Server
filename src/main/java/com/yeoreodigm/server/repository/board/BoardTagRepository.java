package com.yeoreodigm.server.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardTag;
import com.yeoreodigm.server.domain.board.HashTag;
import com.yeoreodigm.server.domain.board.QBoardTag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.board.QBoardTag.boardTag;

@Repository
@AllArgsConstructor
public class BoardTagRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(BoardTag boardTag) {
        em.persist(boardTag);
    }

    public void saveAndFlush(BoardTag boardTag) {
        save(boardTag);
        em.flush();
    }

    public List<BoardTag> findByBoardId(Long boardId) {
        return queryFactory
                .selectFrom(boardTag)
                .where(boardTag.board.id.eq(boardId))
                .fetch();
    }

    public List<HashTag> findHashTagByBoardId(Long boardId) {
        return queryFactory
                .select(boardTag.hashTag)
                .from(boardTag)
                .where(boardTag.board.id.eq(boardId))
                .fetch();
    }

    public List<Board> findBoardByHashTag(HashTag hashTag) {
        return queryFactory
                .select(boardTag.board)
                .from(boardTag)
                .where(boardTag.hashTag.id.eq(hashTag.getId()))
                .fetch();
    }

    public List<HashTag> findPopularHashTag() {
        return queryFactory
                .select(boardTag.hashTag)
                .from(boardTag)
                .groupBy(boardTag.hashTag.id)
                .orderBy(boardTag.hashTag.id.count().desc())
                .fetch();
    }

    public void deleteById(Long boardTagId) {
        queryFactory
                .delete(boardTag)
                .where(boardTag.id.eq(boardTagId))
                .execute();
    }

}
