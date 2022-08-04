package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.CourseComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class CourseCommentRepository {

    private final EntityManager em;

    public void save(CourseComment courseComment) {
        em.persist(courseComment);
    }

    public void saveAndFlush(CourseComment courseComment) {
        em.persist(courseComment);
        em.flush();
    }

}
