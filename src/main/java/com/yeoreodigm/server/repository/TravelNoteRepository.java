package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.QTravelNoteLike;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QTravelNote.travelNote;
import static com.yeoreodigm.server.domain.QTravelNoteLike.*;

@Repository
@RequiredArgsConstructor
public class TravelNoteRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(TravelNote travelNote) {
        em.persist(travelNote);
    }

    public void flush() {
        em.flush();
    }

    public void merge(TravelNote travelNote) {
        em.merge(travelNote);
    }

    public void saveAndFlush(TravelNote travelNote) {
        save(travelNote);
        em.flush();
    }

    public List<TravelNote> findAll() {
        return queryFactory
                .selectFrom(travelNote)
                .orderBy(travelNote.id.asc())
                .fetch();
    }

    public Long countAll() {
        return queryFactory
                .select(travelNote.count())
                .from(travelNote)
                .fetchOne();
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

    public List<TravelNote> findPublicLimiting(int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true))
                .orderBy(travelNote.id.asc())
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicPagingAndLimiting(int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true))
                .orderBy(travelNote.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByMember(Member member, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.member.id.eq(member.getId()), travelNote.publicShare.eq(true))
                .orderBy(travelNote.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordPaging(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true), travelNote.title.lower().contains(keyword.toLowerCase()))
                .orderBy(travelNote.id.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordOrderByModifiedAsc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true), travelNote.title.lower().contains(keyword.toLowerCase()))
                .orderBy(travelNote.modifiedTime.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordOrderByModifiedDesc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.publicShare.eq(true), travelNote.title.lower().contains(keyword.toLowerCase()))
                .orderBy(travelNote.modifiedTime.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordOrderByLikeAsc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .leftJoin(travelNoteLike)
                .on(travelNoteLike.travelNoteId.eq(travelNote.id))
                .where(travelNote.publicShare.eq(true), travelNote.title.lower().contains(keyword.toLowerCase()))
                .groupBy(travelNote.id, travelNoteLike.travelNoteId)
                .orderBy(travelNote.count().asc(), travelNoteLike.travelNoteId.asc().nullsFirst())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordOrderByLikeDesc(String keyword, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .leftJoin(travelNoteLike)
                .on(travelNoteLike.travelNoteId.eq(travelNote.id))
                .where(travelNote.publicShare.eq(true), travelNote.title.lower().contains(keyword.toLowerCase()))
                .groupBy(travelNote.id, travelNoteLike.travelNoteId)
                .orderBy(travelNote.count().desc(), travelNoteLike.travelNoteId.desc().nullsLast())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findPublicByKeywordNonLike(String keyword, int page, int limit) {
        return null;
    }

    public List<TravelNote> findByMember(Member member, int page, int limit) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.member.id.eq(member.getId()))
                .orderBy(travelNote.modifiedTime.desc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<TravelNote> findAllByMember(Member member) {
        return queryFactory
                .selectFrom(travelNote)
                .where(travelNote.member.id.eq(member.getId()))
                .fetch();
    }

}
