package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.MemberPicture;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@AllArgsConstructor
public class MemberPictureRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(MemberPicture memberPicture) {
        em.persist(memberPicture);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(MemberPicture memberPicture) {
        save(memberPicture);
        flush();
    }

}
