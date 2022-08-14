package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.NoteCommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QNoteCommentLike.noteCommentLike;

@Repository
@RequiredArgsConstructor
public class NoteCommentLikeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(NoteCommentLike noteCommentLike) {
        em.persist(noteCommentLike);
    }

    public void saveAndFlush(NoteCommentLike noteCommentLike) {
        save(noteCommentLike);
        em.flush();
    }

    public Long countByNoteCommentId(Long noteCommentId) {
        return queryFactory
                .select(noteCommentLike.count())
                .from(noteCommentLike)
                .where(noteCommentLike.noteCommentId.eq(noteCommentId))
                .fetchOne();
    }

    public NoteCommentLike findByNoteCommentIdAndMemberId(Long noteCommentId, Long memberId) {
        return queryFactory
                .selectFrom(noteCommentLike)
                .where(noteCommentLike.noteCommentId.eq(noteCommentId), noteCommentLike.memberId.eq(memberId))
                .fetchOne();
    }

    public void deleteById(Long noteCommentLikeId) {
        queryFactory
                .delete(noteCommentLike)
                .where(noteCommentLike.id.eq(noteCommentLikeId))
                .execute();
    }
}
