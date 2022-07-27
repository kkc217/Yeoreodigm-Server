package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.QPlaces;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlaces.*;

@Repository
@RequiredArgsConstructor
public class PlacesRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public Places findByPlacesId(Long placeId) {
        try {
            return em.createQuery("select p from Places p where p.id = :placeId", Places.class)
                    .setParameter("placeId", placeId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Places> findByPlacesIdList(List<Long> placeIdList) {
        return em.createQuery("select p from Places p where p.id in :placeIdList", Places.class)
                .setParameter("placeIdList", placeIdList)
                .getResultList();
    }

    public List<Places> findByTitlePaging(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(places)
                .where(places.title.contains(keyword))
                .orderBy(places.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
