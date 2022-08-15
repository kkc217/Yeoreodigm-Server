package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.constraint.EnvConst;
import com.yeoreodigm.server.dto.note.OptimizedCourseDto;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final PlaceService placeService;

    @Transactional
    public void saveNewCourse(TravelNote travelNote, int day, List<Long> places) {
        Course course = new Course(travelNote, day, places);
        courseRepository.save(course);
    }

    @Transactional
    public void saveNewCoursesByRecommend(TravelNote travelNote, List<List<Long>> recommendedCourse) {
        for (int i = 0; i < recommendedCourse.size(); i++) {
            saveNewCourse(travelNote, i + 1, recommendedCourse.get(i));
        }
    }

    public Course getCourseByTravelNoteAndDay(TravelNote travelNote, int day) {
        return courseRepository.findByTravelNoteIdAndDay(travelNote.getId(), day);
    }

    public List<Course> getCoursesByTravelNote(TravelNote travelNote) {
        return courseRepository.findCoursesByTravelNoteId(travelNote.getId());
    }

    public List<Course> getCoursesByTravelNotePaging(TravelNote travelNote, int page, int limit) {
        return courseRepository.findCoursesByTravelNoteIdPaging(travelNote.getId(), limit * (page - 1), limit);
    }

    public int checkNextCoursePage(TravelNote travelNote, int page, int limit) {
        return getCoursesByTravelNotePaging(travelNote, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public void addPlace(TravelNote travelNote, int day, Long placeId) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNote.getId(), day);

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
    public void addPlaces(TravelNote travelNote, int day, List<Long> placeIdList) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNote.getId(), day);

        if (course != null) {
            List<Long> places = course.getPlaces();
            places.addAll(placeIdList);
            course.changePlaces(places);
            courseRepository.saveAndFlush(course);
        } else {
            throw new BadRequestException("일치하는 코스 정보가 없습니다.");
        }
    }

    @Transactional
    public RouteInfoDto getRouteInfoByCourse(Course course) {
        if (course == null) throw new BadRequestException("일치하는 일차 정보가 없습니다.");

        List<RouteInfo> routeInfoList = new ArrayList<>();

        List<Long> placeList = course.getPlaces();

        for (int i = 0; i < placeList.size() - 1; i++) {
            routeInfoList.add(placeService.getRouteInfo(placeList.get(i), placeList.get(i + 1)));
        }

        return new RouteInfoDto(course.getDay(), routeInfoList);
    }

    @Transactional
    public List<RouteInfoDto> getRouteInfosByTravelNote(TravelNote travelNote) {
        List<RouteInfoDto> result = new ArrayList<>();

        List<Course> courseList = getCoursesByTravelNote(travelNote);

        for (Course course : courseList) {
            result.add(getRouteInfoByCourse(course));
        }

        return result;
    }

    @Transactional
    public List<RouteInfoDto> getRouteInfosByCourseList(List<Course> courseList) {
        List<RouteInfoDto> result = new ArrayList<>();

        for (Course course : courseList) {
            result.add(getRouteInfoByCourse(course));
        }

        return result;
    }

    @Transactional
    public void optimizeCourse(TravelNote travelNote) {
        List<Course> courseList = courseRepository.findCoursesByTravelNoteId(travelNote.getId());

        List<Long> placeIdList = new ArrayList<>();
        for (Course course : courseList) {
            placeIdList.addAll(course.getPlaces());
        }

        List<List<Long>> optimizedCourse = getOptimizedCourses(courseList.size(), placeIdList);

        for (int i = 0; i < courseList.size(); i++) {
            Course course = courseList.get(i);
            course.changePlaces(optimizedCourse.get(i));
            courseRepository.save(course);
        }
        courseRepository.flush();
    }

    public List<List<Long>> getOptimizedCourses(int totalDay, List<Long> placeIdList) {
        if (placeIdList.size() == 0) {
            return new ArrayList<>();
        }

        StringBuilder placeIdString = new StringBuilder();

        for (Long placeId : placeIdList) {
            placeIdString.append(placeId).append(",");
        }

        WebClient webClient = WebClient.create(EnvConst.COURSE_OPTIMIZE_URL);

        Mono<OptimizedCourseDto> apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.COURSE_OPTIMIZE_URI)
                        .queryParam("day", totalDay)
                        .queryParam("placeList", placeIdString.substring(0, placeIdString.length() - 1))
                        .build())
                .retrieve()
                .bodyToMono(OptimizedCourseDto.class);

        try {
            return Objects.requireNonNull(apiResult.block()).getResult();
        } catch (WebClientResponseException | NullPointerException e) {
            throw new BadRequestException("경로 최적화에 실패하였습니다.");
        }

    }

}