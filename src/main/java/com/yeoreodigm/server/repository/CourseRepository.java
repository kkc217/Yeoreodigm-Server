package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QCourse.*;

@Repository
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Course course) {
        em.persist(course);
    }

    public void saveAndFlush(Course course) {
        em.persist(course);
        em.flush();
    }

    public void flush() {
        em.flush();
    }

    public List<Course> findByTravelNoteIdPaging(Long travelNoteId, int page, int limit) {
        return queryFactory
                .selectFrom(course)
                .where(course.travelNote.id.eq(travelNoteId))
                .orderBy(course.day.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

    public List<Course> findByTravelNoteId(Long travelNoteId) {
        return queryFactory
                .selectFrom(course)
                .where(course.travelNote.id.eq(travelNoteId))
                .orderBy(course.day.asc())
                .fetch();
    }

    public Course findByTravelNoteIdAndDay(Long travelNoteId, int day) {
        try {
            return queryFactory
                    .selectFrom(course)
                    .where(course.travelNote.id.eq(travelNoteId), course.day.eq(day))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 코스가 둘 이상입니다.");
        }
    }

    public void deleteByCourse(Course target) {
        queryFactory
                .delete(course)
                .where(course.eq(target))
                .execute();
    }

}
