package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlaceComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlaceComment.placeComment;

@Repository
@RequiredArgsConstructor
public class PlaceCommentRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(PlaceComment placeComment) {
        em.persist(placeComment);
    }

    public void saveAndFlush(PlaceComment placeComment) {
        save(placeComment);
        em.flush();
    }

    public PlaceComment findById(Long placeCommentId) {
        return queryFactory
                .selectFrom(placeComment)
                .where(placeComment.id.eq(placeCommentId))
                .fetchOne();
    }

    public List<PlaceComment> findPlaceCommentsByPlaceId(Long placeId) {
        return queryFactory
                .selectFrom(placeComment)
                .where(placeComment.placeId.eq(placeId))
                .orderBy(placeComment.created.asc())
                .fetch();
    }

    public void deleteById(Long placeCommentId) {
        queryFactory
                .delete(placeComment)
                .where(placeComment.id.eq(placeCommentId))
                .execute();
    }

}
