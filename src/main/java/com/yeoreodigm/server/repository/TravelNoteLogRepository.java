package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.TravelNoteLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QTravelNoteLog.travelNoteLog;

@Repository
@RequiredArgsConstructor
public class TravelNoteLogRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(TravelNoteLog travelNoteLog) {
        em.persist(travelNoteLog);
    }

    public void saveAndFlush(TravelNoteLog travelNoteLog) {
        save(travelNoteLog);
        em.flush();
    }

    public TravelNoteLog findByTravelNoteIdAndMemberId(Long travelNoteId, Long memberId) {
        return queryFactory
                .selectFrom(travelNoteLog)
                .where(travelNoteLog.travelNoteId.eq(travelNoteId), travelNoteLog.memberId.eq(memberId))
                .fetchOne();
    }

}
