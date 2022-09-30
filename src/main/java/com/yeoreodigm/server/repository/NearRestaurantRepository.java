package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.NearRestaurant;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QNearRestaurant.nearRestaurant;

@Repository
@RequiredArgsConstructor
public class NearRestaurantRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public NearRestaurant findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(nearRestaurant)
                    .where(nearRestaurant.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 음식점이 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdKorean(Long placeId) {
        return queryFactory
                .select(nearRestaurant.korean)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

    public List<Long> findByPlaceIdChinese(Long placeId) {
        return queryFactory
                .select(nearRestaurant.chinese)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

    public List<Long> findByPlaceIdJapanese(Long placeId) {
        return queryFactory
                .select(nearRestaurant.japanese)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

    public List<Long> findByPlaceIdWestern(Long placeId) {
        return queryFactory
                .select(nearRestaurant.western)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

    public List<Long> findByPlaceIdBunsik(Long placeId) {
        return queryFactory
                .select(nearRestaurant.bunsik)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

    public List<Long> findByPlaceIdCafe(Long placeId) {
        return queryFactory
                .select(nearRestaurant.cafe)
                .from(nearRestaurant)
                .where(nearRestaurant.placeId.eq(placeId))
                .fetchOne();
    }

}
