package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QCourse.course;
import static com.yeoreodigm.server.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(Member member) {
        save(member);
        em.flush();
    }

    public void merge(Member member) {
        em.merge(member);
    }

    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public Member findByEmail(String email) {
        try {
            return queryFactory
                    .selectFrom(member)
                    .where(member.email.eq(email))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 이메일이 둘 이상입니다.");
        }
    }

    public Member findByNickname(String nickname) {
        try {
            return queryFactory
                    .selectFrom(member)
                    .where(member.nickname.eq(nickname))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 닉네임이 둘 이상입니다.");
        }
    }

    public void deleteMember(Member target) {
        queryFactory
                .delete(member)
                .where(member.id.eq(target.getId()))
                .execute();
    }
}
