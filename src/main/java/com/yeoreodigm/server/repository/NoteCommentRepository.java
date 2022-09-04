package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.NoteComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QNoteComment.noteComment;

@Repository
@RequiredArgsConstructor
public class NoteCommentRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(NoteComment noteComment) {
        em.persist(noteComment);
    }

    public void saveAndFlush(NoteComment noteComment) {
        save(noteComment);
        em.flush();
    }

    public NoteComment findById(Long noteCommentId) {
        return queryFactory
                .selectFrom(noteComment)
                .where(noteComment.id.eq(noteCommentId))
                .fetchOne();
    }

    public List<NoteComment> findByTravelNoteID(Long travelNoteId) {
        return queryFactory
                .selectFrom(noteComment)
                .where(noteComment.travelNoteId.eq(travelNoteId))
                .orderBy(noteComment.created.asc())
                .fetch();
    }

    public void deleteById(Long commentId) {
        queryFactory
                .delete(noteComment)
                .where(noteComment.id.eq(commentId))
                .execute();
    }
}
