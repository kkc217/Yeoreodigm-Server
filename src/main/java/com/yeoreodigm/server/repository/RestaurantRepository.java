package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Restaurant;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QRestaurant.restaurant;

@Repository
@RequiredArgsConstructor
public class RestaurantRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public Restaurant findById(Long restaurantId) {
        try {
            return queryFactory
                    .selectFrom(restaurant)
                    .where(restaurant.id.eq(restaurantId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 음식점이 둘 이상입니다.");
        }
    }

}
