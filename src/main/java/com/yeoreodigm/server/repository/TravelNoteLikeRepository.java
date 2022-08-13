package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.TravelNoteLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QTravelNoteLike.travelNoteLike;

@Repository
@RequiredArgsConstructor
public class TravelNoteLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(TravelNoteLike travelNoteLike) {
        em.persist(travelNoteLike);
    }

    public void saveAndFlush(TravelNoteLike travelNoteLike) {
        save(travelNoteLike);
        em.flush();
    }

    public Long countByTravelNoteId(Long travelNoteId) {
        return queryFactory
                .select(travelNoteLike.count())
                .from(travelNoteLike)
                .where(travelNoteLike.travelNoteId.eq(travelNoteId))
                .fetchOne();
    }

    public TravelNoteLike findByTravelNoteIdAndMemberId(Long travelNoteId, Long memberId) {
        return queryFactory
                .selectFrom(travelNoteLike)
                .where(travelNoteLike.travelNoteId.eq(travelNoteId), travelNoteLike.memberId.eq(memberId))
                .fetchOne();
    }

}
