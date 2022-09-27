package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Companion;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.hibernate.NonUniqueObjectException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QCompanion.companion;

@Repository
@RequiredArgsConstructor
public class CompanionRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Companion companion) {
        em.persist(companion);
    }

    public void saveAndFlush(Companion companion) {
        save(companion);
        em.flush();
    }

    public List<Companion> findCompanionsByTravelNote(TravelNote travelNote) {
        return queryFactory
                .selectFrom(companion)
                .where(companion.travelNote.id.eq(travelNote.getId()))
                .fetch();
    }

    public List<Long> findMemberIdsByTravelNote(TravelNote travelNote) {
        return queryFactory
                .select(companion.member.id)
                .from(companion)
                .where(companion.travelNote.id.eq(travelNote.getId()))
                .fetch();
    }

    public Companion findByTravelNoteAndMember(TravelNote travelNote, Member member) {
        try {
            return queryFactory
                    .selectFrom(companion)
                    .where(companion.travelNote.id.eq(travelNote.getId()), companion.member.id.eq(member.getId()))
                    .fetchOne();
        } catch (NonUniqueObjectException e) {
            throw new BadRequestException("일치하는 동행자 정보가 둘 이상입니다.");
        }
    }

    public Companion findByTravelNoteAndMemberId(TravelNote travelNote, Long memberId) {
        try {
            return queryFactory
                    .selectFrom(companion)
                    .where(companion.travelNote.id.eq(travelNote.getId()), companion.member.id.eq(memberId))
                    .fetchOne();
        } catch (NonUniqueObjectException e) {
            throw new BadRequestException("일치하는 동행자 정보가 둘 이상입니다.");
        }
    }

    public void deleteCompanion(Companion target) {
        queryFactory
                .delete(companion)
                .where(companion.id.eq(target.getId()))
                .execute();
    }

}
