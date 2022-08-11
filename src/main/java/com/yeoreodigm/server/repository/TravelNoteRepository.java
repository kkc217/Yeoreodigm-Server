package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.QTravelNote;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static com.yeoreodigm.server.domain.QTravelNote.travelNote;

@Repository
@RequiredArgsConstructor
public class TravelNoteRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(TravelNote travelNote) {
        em.persist(travelNote);
    }

    public void saveAndFlush(TravelNote travelNote) {
        em.persist(travelNote);
        em.flush();
    }

    public TravelNote findById(Long id) {
        try {
            return queryFactory
                    .selectFrom(travelNote)
                    .where(travelNote.id.eq(id))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 메이킹 노트가 둘 이상입니다.");
        }
    }

    public TravelNote findByMemberAndId(Member member, Long id) {
        try {
            return queryFactory
                    .selectFrom(travelNote)
                    .where(travelNote.member.eq(member), travelNote.id.eq(id))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 메이킹 노트가 둘 이상입니다.");
        }
    }

    public List<TravelNote> findByPublicLimiting(int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true))
                .limit(limit)
                .fetch();
    }

}
