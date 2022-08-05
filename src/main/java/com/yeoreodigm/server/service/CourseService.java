package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.TravelNote;
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

    @Transactional
    public void saveCourse(TravelNote travelNote, int day, List<Long> places) {
        Course course = new Course(travelNote, day, places);
        courseRepository.saveAndFlush(course);
    }

    public List<Course> searchCoursePaging(Long travelNoteId, int page, int limit) {
        return courseRepository.findByTravelNoteIdPaging(travelNoteId, limit * (page - 1), limit);
    }

    public List<Course> searchCourse(Long travelNoteId) {
        return courseRepository.findByTravelNoteId(travelNoteId);
    }

    public int checkNextCoursePage(Long travelNoteId, int page, int limit) {
        return searchCoursePaging(travelNoteId, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

}