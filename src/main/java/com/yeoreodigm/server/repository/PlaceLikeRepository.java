package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceLikeRepository {

    private final EntityManager em;

    public void save(PlaceLike placeLike) {
        em.persist(placeLike);
    }

    public void saveAndFlush(PlaceLike placeLike) {
        em.persist(placeLike);
        em.flush();
    }

    public List<PlaceLike> findByMember(Member member) {
        return em.createQuery("select pl from PlaceLike pl where pl.member = :member", PlaceLike.class)
                .setParameter("member", member)
                .getResultList();
    }

    public List<PlaceLike> findByMemberPaging(Member member, int page) {
        return em.createQuery("select pl from PlaceLike pl where pl.member = :member order by pl.id", PlaceLike.class)
                .setParameter("member", member)
                .setFirstResult(page)
                .setMaxResults(QueryConst.MAX_RESULTS)
                .getResultList();
    }

}
