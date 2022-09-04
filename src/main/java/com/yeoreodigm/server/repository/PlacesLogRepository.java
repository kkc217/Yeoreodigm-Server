package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
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

    public PlacesLog findByPlaceAndMember(Places place, Member member) {
        try {
            return queryFactory.selectFrom(placesLog)
                    .where(placesLog.placeId.eq(place.getId()), placesLog.memberId.eq(member.getId()))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("여행지 정보를 불러오는데 실패하였습니다.");
        }
    }

}
