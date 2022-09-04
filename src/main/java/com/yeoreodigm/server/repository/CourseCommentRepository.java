package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QCourseComment.courseComment;

@Repository
@RequiredArgsConstructor
public class CourseCommentRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(CourseComment courseComment) {
        em.persist(courseComment);
    }

    public void saveAndFlush(CourseComment courseComment) {
        save(courseComment);
        em.flush();
    }

    public CourseComment findById(Long id) {
        try {
            return queryFactory
                    .selectFrom(courseComment)
                    .where(courseComment.id.eq(id))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 코멘트가 둘 이상입니다.");
        }
    }

    public List<CourseComment> findCourseCommentsByCourse(Course course) {
        return queryFactory
                .selectFrom(courseComment)
                .where(courseComment.course.eq(course))
                .orderBy(courseComment.modified.asc())
                .fetch();
    }

    public void deleteByCourseCommentId(Long courseCommentId) {
        queryFactory
                .delete(courseComment)
                .where(courseComment.id.eq(courseCommentId))
                .execute();
    }

}
