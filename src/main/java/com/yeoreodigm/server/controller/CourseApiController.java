package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.accommodation.AccommodationDto;
import com.yeoreodigm.server.dto.accommodation.AccommodationListDto;
import com.yeoreodigm.server.dto.course.*;
import com.yeoreodigm.server.dto.route.RouteItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseApiController {

    private final CourseService courseService;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    private final RouteInfoService routeInfoService;

    private final MapMarkerService mapMarkerService;

    private final AccommodationService accommodationService;

    @GetMapping("/{travelNoteId}")
    public Result<List<CourseItemDto>> callCourseInfos(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);

        List<CourseItemDto> response = new ArrayList<>();
        int indexStart = 0;
        for (Course course : courseList) {
            response.add(new CourseItemDto(
                    indexStart,
                    course.getDay(),
                    placeService.getPlacesByCourse(course)));
            indexStart += course.getPlaces().size();
        }

        return new Result<>(response);
    }

    @GetMapping("/route/{travelNoteId}")
    public Result<List<RouteItemDto>> callRouteInfos(
            @PathVariable("travelNoteId") Long travelNoteId) {
        List<Course> courseList
                = courseService.getCoursesByTravelNote(travelNoteService.getTravelNoteById(travelNoteId));

        List<RouteItemDto> response = new ArrayList<>();
        for (Course course : courseList) {
            if (course == null) throw new BadRequestException("일치하는 일차의 정보가 없습니다.");

            List<Long> placeIdList = course.getPlaces();
            List<RouteInfo> routeInfoList = new ArrayList<>();
            for (int i = 0; i < placeIdList.size() - 1; i++) {
                routeInfoList.add(routeInfoService.getRouteInfo(placeIdList.get(i), placeIdList.get(i + 1)));
            }
            response.add(new RouteItemDto(course.getDay(), routeInfoService.getRouteData(routeInfoList)));
        }
        return new Result<>(response);
    }

    @GetMapping("/{travelNoteId}/{day}")
    public Result<CourseRouteDto> callCourseInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("day") int day) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
        Course course = courseService.getCourseByTravelNoteAndDay(travelNote, day);

        if (course == null) throw new BadRequestException("일치하는 코스 정보가 없습니다.");

        List<Long> placeIdList = course.getPlaces();
        List<RouteInfo> routeInfoList = new ArrayList<>();
        for (int i = 0; i < placeIdList.size() - 1; i++) {
            routeInfoList.add(routeInfoService.getRouteInfo(placeIdList.get(i), placeIdList.get(i + 1)));
        }
        RouteItemDto routeItemDto = new RouteItemDto(course.getDay(), routeInfoService.getRouteData(routeInfoList));

        return new Result<>(new CourseRouteDto(
                0,
                course.getDay(),
                travelNote.getCourses().size(),
                placeService.getPlacesByCourse(course),
                routeItemDto.getRouteInfos()));
    }

    @GetMapping("/coordinate/{travelNoteId}")
    public Result<List<CoordinateItemDto>> callCoordinateInfos(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
        List<String> markerColorList = mapMarkerService.getMarkerColors(courseList.size());

        List<CoordinateItemDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CoordinateItemDto(
                    course.getDay(),
                    markerColorList.get(course.getDay() - 1),
                    placeService.getPlacesByCourse(course)));
        }

        return new Result<>(response);
    }

    @PutMapping("")
    public void saveCourses(
            @RequestBody @Valid SaveCourseRequestDto requestDto) {
        travelNoteService.updateCourse(travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.getCourseList());
    }

    @PatchMapping("")
    public void addPlaces(
            @RequestBody @Valid RecommendPlaceRequestDto requestDto) {
        courseService.addPlaces(
                travelNoteService.getTravelNoteById(
                        requestDto.getTravelNoteId()), requestDto.getDay(), requestDto.getPlaceIdList());
    }

    @PostMapping("/optimize")
    public void optimizeCourse(
            @RequestBody HashMap<String, Long> request) {
        courseService.optimizeCourse(travelNoteService.getTravelNoteById(request.get("travelNoteId")));
    }

    @GetMapping("/accommodation")
    public AccommodationListDto callNearAccommodation(
            @RequestParam("travelNoteId") Long travelNoteId,
            @RequestParam(name = "day", required = false, defaultValue = "1") int day,
            @RequestParam(name = "type", required = false, defaultValue = "0") int type,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
        Course course = courseService.getCourseByTravelNoteAndDay(travelNote, day);
        if (Objects.isNull(course)) throw new BadRequestException("일차를 확인해주세요.");

        List<Long> placeIdList = course.getPlaces();

        if (placeIdList.size() == 0)
            return new AccommodationListDto(
                    day, travelNote.getCourses().size(), new PageResult<>(new ArrayList<>(), 0));

        List<Long> accommodationIdList = accommodationService.getNearAccommodationId(
                placeIdList.get(placeIdList.size() - 1), type);

        return new AccommodationListDto(
                day,
                travelNote.getCourses().size(),
                new PageResult<>(
                        accommodationService.getAccommodationPaging(accommodationIdList, page, limit)
                                .stream()
                                .map(AccommodationDto::new)
                                .toList(),
                        accommodationService.checkNextAccommodations(accommodationIdList, page, limit)));
    }

}
