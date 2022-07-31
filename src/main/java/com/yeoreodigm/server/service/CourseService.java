package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> searchCourse(Long travelNoteId, int page, int limit) {
        return courseRepository.findByTravelNoteIdPaging(travelNoteId, limit * (page - 1), limit);
    }

    public int checkNextCoursePage(Long travelNoteId, int page, int limit) {
        return searchCourse(travelNoteId, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

}
