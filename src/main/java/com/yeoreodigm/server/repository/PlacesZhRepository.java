package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlacesZh;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;

import static com.yeoreodigm.server.domain.QPlacesZh.*;

@Repository
@RequiredArgsConstructor
public class PlacesZhRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public PlacesZh findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(placesZh)
                    .where(placesZh.place.id.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행지가 둘 이상입니다.");
        }
    }

}
