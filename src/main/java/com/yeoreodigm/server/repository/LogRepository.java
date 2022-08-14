package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlacesLog;
import com.yeoreodigm.server.domain.QPlacesLog;
import com.yeoreodigm.server.domain.QTravelNoteLog;
import com.yeoreodigm.server.domain.TravelNoteLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlacesLog.placesLog;
import static com.yeoreodigm.server.domain.QTravelNoteLog.travelNoteLog;

@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void savePlacesLog(PlacesLog placesLog) {
        em.persist(placesLog);
    }

    public void saveAndFlushPlacesLog(PlacesLog placesLog) {
        savePlacesLog(placesLog);
        em.flush();
    }

    public void saveNoteLog(TravelNoteLog travelNoteLog) {
        em.persist(travelNoteLog);
    }

    public void saveAndFlushNoteLog(TravelNoteLog travelNoteLog) {
        saveNoteLog(travelNoteLog);
        em.flush();
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

    public List<Long> findMostPlaceIdLimiting(int limit) {
        return queryFactory
                .select(placesLog.placeId)
                .from(placesLog)
                .groupBy(placesLog.placeId)
                .orderBy(placesLog.placeId.count().desc())
                .limit(limit)
                .fetch();
    }

}
