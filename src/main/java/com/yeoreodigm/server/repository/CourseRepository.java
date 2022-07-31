package com.yeoreodigm.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Course;
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

    public List<Course> findByTravelNoteIdPaging(Long travelNoteId, int page, int limit) {
        return queryFactory
                .selectFrom(course)
                .where(course.travelNote.id.eq(travelNoteId))
                .orderBy(course.day.asc())
                .offset(page)
                .limit(limit)
                .fetch();
    }

}
