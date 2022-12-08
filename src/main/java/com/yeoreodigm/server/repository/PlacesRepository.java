package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlaceLike.placeLike;
import static com.yeoreodigm.server.domain.QPlaces.places;

@Repository
@RequiredArgsConstructor
public class PlacesRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public List<Places> findAll() {
        return queryFactory
                .selectFrom(places)
                .orderBy(places.id.asc())
                .fetch();
    }
    public Places findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(places)
                    .where(places.id.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 여행지가 둘 이상입니다.");
        }
    }

    public List<Places> findPlacesByKeywordPaging(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(places)
                .where(places.title.lower().contains(keyword.toLowerCase()))
                .orderBy(places.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Places> findByKeywordOrderByLikeAsc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(places)
                .leftJoin(placeLike)
                .on(placeLike.placeId.eq(places.id))
                .where(places.title.lower().contains(keyword.toLowerCase()))
                .groupBy(places.id, placeLike.placeId)
                .orderBy(places.count().asc(), placeLike.placeId.asc().nullsFirst())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Places> findByKeywordOrderByLikeDesc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(places)
                .leftJoin(placeLike)
                .on(placeLike.placeId.eq(places.id))
                .where(places.title.lower().contains(keyword.toLowerCase()))
                .groupBy(places.id, placeLike.placeId)
                .orderBy(places.count().desc(), placeLike.placeId.desc().nullsLast())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public String findOneImageUrl(int page) {
        return queryFactory
                .select(places.imageUrl)
                .from(places)
                .offset(page)
                .limit(1)
                .fetchOne();
    }

    public List<Places> findPagingAndLimiting(int page, int limit) {
        return queryFactory
                .selectFrom(places)
                .orderBy(places.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
