package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlacesEn;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;

import static com.yeoreodigm.server.domain.QPlacesEn.placesEn;

@Repository
@RequiredArgsConstructor
public class PlacesEnRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public PlacesEn findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(placesEn)
                    .where(placesEn.place.id.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행지가 둘 이상입니다.");
        }
    }

}
