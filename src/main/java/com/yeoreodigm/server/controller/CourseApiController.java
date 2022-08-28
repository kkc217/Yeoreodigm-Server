package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.course.SaveCourseRequestDto;
import com.yeoreodigm.server.dto.note.CoordinateItemDto;
import com.yeoreodigm.server.dto.note.RecommendPlaceRequestDto;
import com.yeoreodigm.server.dto.note.RouteItemDto;
import com.yeoreodigm.server.dto.course.CoursItemDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseApiController {

    private final CourseService courseService;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    private final MapMarkerService mapMarkerService;

    @GetMapping("/{travelNoteId}")
    public Result<List<CoursItemDto>> callCourseInfos(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);

        List<CoursItemDto> response = new ArrayList<>();
        int indexStart = 0;
        for (Course course : courseList) {
            response.add(new CoursItemDto(
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
        return new Result<>(
                courseService.getRouteInfosByTravelNote(travelNoteService.getTravelNoteById(travelNoteId)));
    }

    @GetMapping("/{travelNoteId}/{day}")
    public Result<CoursItemDto> callCourseInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("day") int day) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
        Course course = courseService.getCourseByTravelNoteAndDay(travelNote, day);

        return new Result<>(
                new CoursItemDto(0, course.getDay(), placeService.getPlacesByCourse(course)));
    }

    @GetMapping("/route/{travelNoteId}/{day}")
    public Result<RouteItemDto> callRouteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("day") int day) {
        return new Result<>(
                courseService.getRouteInfoByTravelNoteAndDay(travelNoteService.getTravelNoteById(travelNoteId), day));
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

}
