package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QPhotodigm.photodigm;

@Repository
@AllArgsConstructor
public class PhotodigmRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Photodigm photodigm) {
        em.persist(photodigm);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(Photodigm photodigm) {
        save(photodigm);
        flush();
    }

    public Photodigm findById(Long photodigmId) {
        try {
            return queryFactory
                    .selectFrom(photodigm)
                    .where(photodigm.id.eq(photodigmId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 포토다임이 둘 이상입니다.");
        }
    }

    public List<Photodigm> findByMember(Member member) {
        return queryFactory
                .selectFrom(photodigm)
                .where(photodigm.member.id.eq(member.getId()))
                .fetch();
    }

    public List<Photodigm> findByMemberPaging(Member member, int page, int limit) {
        return queryFactory
                .selectFrom(photodigm)
                .where(photodigm.member.id.eq(member.getId()))
                .orderBy(photodigm.modified.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
