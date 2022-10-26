package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlacesExtraInfo;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;

import static com.yeoreodigm.server.domain.QPlacesExtraInfo.placesExtraInfo;

@Repository
@RequiredArgsConstructor
public class PlacesExtraInfoRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public PlacesExtraInfo findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(placesExtraInfo)
                    .where(placesExtraInfo.place.id.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행지 상세 정보가 둘 이상입니다.");
        }
    }

}
