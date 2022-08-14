package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.MapMarker;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QMapMarker.*;

@Repository
@RequiredArgsConstructor
public class MapMarkerRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public List<String> findMarkerColorsByTotalDay(int totalDay) {
        return queryFactory
                .select(mapMarker.color)
                .from(mapMarker)
                .where(mapMarker.day.loe(totalDay))
                .fetch();
    }


    public String findMarkerColorByDay(int day) {
        try {
            MapMarker result = queryFactory
                    .selectFrom(mapMarker)
                    .where(mapMarker.day.eq(day))
                    .fetchOne();
            if (result != null) {
                return result.getColor();
            } else {
                throw new BadRequestException("일치하는 마커 정보가 없습니다.");
            }
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 마커 정보가 둘 이상입니다.");
        }
    }

}
