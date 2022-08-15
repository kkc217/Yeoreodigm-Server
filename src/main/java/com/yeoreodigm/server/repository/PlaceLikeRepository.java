package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPlaceLike.*;

@Repository
@RequiredArgsConstructor
public class PlaceLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(PlaceLike placeLike) {
        em.persist(placeLike);
    }

    public void saveAndFlush(PlaceLike placeLike) {
        save(placeLike);
        em.flush();
    }

    public List<PlaceLike> findByMember(Member member) {
        return queryFactory
                .selectFrom(placeLike)
                .where(placeLike.member.eq(member))
                .fetch();
    }

    public List<PlaceLike> findByMemberPaging(Member member, int page, int limit) {
        return queryFactory
                .selectFrom(placeLike)
                .where(placeLike.member.eq(member))
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public Long countByPlaceId(Long placeId) {
        return queryFactory
                .select(placeLike.count())
                .from(placeLike)
                .where(placeLike.places.id.eq(placeId))
                .fetchOne();
    }

    public PlaceLike findByPlaceIdAndMemberId(Long placeId, Long memberId) {
        return queryFactory
                .selectFrom(placeLike)
                .where(placeLike.places.id.eq(placeId), placeLike.member.id.eq(memberId))
                .fetchOne();
    }
}
