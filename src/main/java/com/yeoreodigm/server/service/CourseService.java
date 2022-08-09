package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final PlaceService placeService;

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

    @Transactional
    public void addPlace(Long travelNoteId, int day, Long placeId) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNoteId, day);

        if (course != null) {
            List<Long> places = course.getPlaces();
            places.add(placeId);
            course.changePlaces(places);
            courseRepository.saveAndFlush(course);
        } else {
            throw new BadRequestException("일치하는 코스 정보가 없습니다.");
        }
    }

    @Transactional
    public void addPlaceList(Long travelNoteId, int day, List<Long> placeIdList) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNoteId, day);

        if (course != null) {
            List<Long> places = course.getPlaces();
            for (Long placeId : placeIdList) {
                places.add(placeId);
                course.changePlaces(places);
                courseRepository.saveAndFlush(course);
            }
        } else {
            throw new BadRequestException("일치하는 코스 정보가 없습니다.");
        }
    }

    @Transactional
    public List<RouteInfo> callRoutes(Long travelNoteId) {
        List<RouteInfo> result = new ArrayList<>();

        List<Course> courseList = searchCourse(travelNoteId);

        for (Course course : courseList) {
            List<Long> placeList = course.getPlaces();
            for (int i = 0; i < placeList.size() - 1; i++) {
                result.add(placeService.callRoute(placeList.get(i), placeList.get(i + 1)));
            }
        }
        return result;
    }
}