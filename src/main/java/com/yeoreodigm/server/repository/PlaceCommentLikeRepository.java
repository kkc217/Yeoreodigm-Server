package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.PlaceCommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QPlaceCommentLike.placeCommentLike;

@Repository
@RequiredArgsConstructor
public class PlaceCommentLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(PlaceCommentLike placeCommentLike) {
        em.persist(placeCommentLike);
    }

    public void saveAndFlush(PlaceCommentLike placeCommentLike) {
        save(placeCommentLike);
        em.flush();
    }

    public Long countByPlaceCommentId(Long placeCommentId) {
        return queryFactory
                .select(placeCommentLike.count())
                .from(placeCommentLike)
                .where(placeCommentLike.placeCommentId.eq(placeCommentId))
                .fetchOne();
    }

    public PlaceCommentLike findByPlaceCommentIdAndMemberId(Long placeCommentId, Long memberId) {
        return queryFactory
                .selectFrom(placeCommentLike)
                .where(placeCommentLike.placeCommentId.eq(placeCommentId), placeCommentLike.memberId.eq(memberId))
                .fetchOne();
    }

    public void deleteById(Long placeCommentId) {
        queryFactory
                .delete(placeCommentLike)
                .where(placeCommentLike.id.eq(placeCommentId))
                .execute();
    }

}
