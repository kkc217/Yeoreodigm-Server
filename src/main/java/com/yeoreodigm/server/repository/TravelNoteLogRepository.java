package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.TravelNoteLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    public TravelNoteLog findByTravelNoteIdAndMemberId(Long travelNoteId, Long memberId) {
        return queryFactory
                .selectFrom(travelNoteLog)
                .where(travelNoteLog.travelNoteId.eq(travelNoteId), travelNoteLog.memberId.eq(memberId))
                .fetchOne();
    }

    public List<Long> findMostNoteIdLimiting(int limit) {
        // 시간 반영하도록 수정하기
        return queryFactory
                .select(travelNoteLog.travelNoteId)
                .from(travelNoteLog)
                .groupBy(travelNoteLog.travelNoteId)
                .orderBy(travelNoteLog.travelNoteId.count().desc())
                .limit(limit)
                .fetch();
    }

}
