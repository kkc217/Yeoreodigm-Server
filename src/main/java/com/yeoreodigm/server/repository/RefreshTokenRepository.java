package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.yeoreodigm.server.domain.QRefreshToken.refreshToken;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(RefreshToken refreshToken) {
        em.persist(refreshToken);
    }

    public void saveAndFlush(RefreshToken refreshToken) {
        save(refreshToken);
        em.flush();
    }

    public Optional<RefreshToken> findByKey(String key) {
        return Optional.ofNullable(queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.key.eq(key))
                .fetchOne());
    }

    public void deleteByKey(String key) {
        queryFactory
                .delete(refreshToken)
                .where(refreshToken.key.eq(key))
                .execute();
    }

}
