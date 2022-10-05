package com.yeoreodigm.server.repository.board;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.board.HashTag;
import com.yeoreodigm.server.domain.board.QHashTag;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static com.yeoreodigm.server.domain.board.QHashTag.hashTag;

@Repository
@AllArgsConstructor
public class HashTagRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(HashTag hashTag) {
        em.persist(hashTag);
    }

    public void saveAndFlush(HashTag hashTag) {
        save(hashTag);
        em.flush();
    }

    public HashTag findById(Long hashTagId) {
        try {
            return queryFactory
                    .selectFrom(hashTag)
                    .where(hashTag.id.eq(hashTagId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 해시태그가 둘 이상입니다.");
        }
    }

    public HashTag findByContent(String content) {
        try {
            return queryFactory
                    .selectFrom(hashTag)
                    .where(hashTag.content.eq(content))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 해시태그가 둘 이상입니다.");
        }
    }

    public List<HashTag> findByKeyword(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(hashTag)
                .where(hashTag.content.contains(keyword))
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
