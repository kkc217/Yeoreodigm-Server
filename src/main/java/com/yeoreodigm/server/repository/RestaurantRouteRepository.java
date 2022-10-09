package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.RestaurantRouteInfo;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QRestaurantRouteInfo.restaurantRouteInfo;

@Repository
@RequiredArgsConstructor
public class RestaurantRouteRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(RestaurantRouteInfo restaurantRouteInfo) {
        em.persist(restaurantRouteInfo);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(RestaurantRouteInfo restaurantRouteInfo) {
        save(restaurantRouteInfo);
        flush();
    }

    public RestaurantRouteInfo findByIds(Long placeId, Long RestaurantId) {
        try {
            return queryFactory
                    .selectFrom(restaurantRouteInfo)
                    .where(restaurantRouteInfo.place.id.eq(placeId),
                            restaurantRouteInfo.restaurant.id.eq(restaurantRouteInfo.id))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 맛집 경로가 둘 이상입니다.");
        }
    }

}
