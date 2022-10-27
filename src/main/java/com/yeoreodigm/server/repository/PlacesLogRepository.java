package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlacesLog;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QPlacesLog.placesLog;

@Repository
@RequiredArgsConstructor
public class PlacesLogRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(PlacesLog placesLog) {
        em.persist(placesLog);
    }

    public void saveAndFlush(PlacesLog placesLog) {
        save(placesLog);
        em.flush();
    }

    public PlacesLog findByPlaceAndMember(Long placeId, Long memberId) {
        try {
            return queryFactory.selectFrom(placesLog)
                    .where(placesLog.placeId.eq(placeId), placesLog.memberId.eq(memberId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("여행지 정보를 불러오는데 실패하였습니다.");
        }
    }

}
