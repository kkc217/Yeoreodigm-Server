package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.dto.constraint.QueryConst;
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
        em.persist(placeLike);
        em.flush();
        em.clear();
    }

    public List<PlaceLike> findByMember(Member member) {
        return queryFactory
                .selectFrom(placeLike)
                .where(placeLike.member.eq(member))
                .fetch();
    }

    public List<PlaceLike> findByMemberPaging(Member member, int page) {
        return queryFactory
                .selectFrom(placeLike)
                .where(placeLike.member.eq(member))
                .offset(page)
                .limit(QueryConst.PAGING_LIMIT)
                .fetch();
    }

}
