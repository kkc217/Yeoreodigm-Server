package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QRouteInfo.routeInfo;

@Repository
@RequiredArgsConstructor
public class RouteInfoRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(RouteInfo routeInfo) {
        em.persist(routeInfo);
    }

    public void saveAndFlush(RouteInfo routeInfo) {
        save(routeInfo);
        em.flush();
    }

    public void flushAndClear() {
        em.flush();
    }

    public RouteInfo findRouteInfoByPlaceIds(Long start, Long goal) {
        try {
            return queryFactory
                    .selectFrom(routeInfo)
                    .where(routeInfo.start.eq(start), routeInfo.goal.eq(goal))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 경로 정보가 둘 이상입니다.");
        }
    }

}
