package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlacesLog.placesLog;

@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

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
