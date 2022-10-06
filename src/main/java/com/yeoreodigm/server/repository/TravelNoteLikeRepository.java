package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNoteLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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

    public void deleteById(Long travelNoteLikeId){
        queryFactory
                .delete(travelNoteLike)
                .where(travelNoteLike.id.eq(travelNoteLikeId))
                .execute();
    }

    public List<TravelNoteLike> findByMemberPaging(Member member, int page, int limit) {
        return queryFactory
                .selectFrom(travelNoteLike)
                .where(travelNoteLike.memberId.eq(member.getId()))
                .orderBy(travelNoteLike.id.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Long> findTravelNoteIdByMemberId(Long memberId) {
        return queryFactory
                .select(travelNoteLike.travelNoteId)
                .from(travelNoteLike)
                .where(travelNoteLike.memberId.eq(memberId))
                .fetch();
    }

    public List<Long> findTravelNoteIdOrderByLikePaging(List<Long> travelNoteIdList, int page, int limit) {
        return queryFactory
                .select(travelNoteLike.travelNoteId)
                .from(travelNoteLike)
                .where(travelNoteLike.travelNoteId.in(travelNoteIdList))
                .groupBy(travelNoteLike.travelNoteId)
                .orderBy(travelNoteLike.travelNoteId.count().desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }
}
