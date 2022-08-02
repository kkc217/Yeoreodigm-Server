package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.note.*;
import com.yeoreodigm.server.service.CourseService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    private final CourseService courseService;

    @GetMapping("/{travelNoteId}")
    public CallNoteInfoResponseDto callNoteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        TravelNote travelNote = travelNoteService.callNote(travelNoteId);
        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(member, travelNote);

        if (noteAuthority == NoteAuthority.ROLE_OWNER) {
            //--------수정하기--------
            List<Places> placesRecommended = placeService.searchPlaces("폭포", 1, 4);
            //----------- -----------
            return new CallNoteInfoResponseDto(noteAuthority, travelNote, placesRecommended);
        } else if (noteAuthority == NoteAuthority.ROLE_COMPANION) {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        } else {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        }
    }

    @GetMapping("/course/{travelNoteId}/{page}")
    public PageResult<List<CallNoteCoursePagingResponseDto>> callNoteCoursePaging(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("page") int page) {

        List<Course> courseList = courseService.searchCoursePaging(
                travelNoteId, page, QueryConst.PAGING_LIMIT_PUBLIC);

        List<CallNoteCoursePagingResponseDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CallNoteCoursePagingResponseDto(course.getDay(), placeService.searchPlacesByCourse(course)));
        }

        int next = courseService.checkNextCoursePage(
                travelNoteId, page, QueryConst.PAGING_LIMIT_PUBLIC);

        return new PageResult<>(response, next);
    }

    @GetMapping("/course/{travelNoteId}")
    public Result<List<CallNoteCourseResponseDto>> callNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId) {

        List<Course> courseList = courseService.searchCourse(travelNoteId);

        List<CallNoteCourseResponseDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CallNoteCourseResponseDto(course.getDay(), placeService.searchPlacesByCourse(course)));
        }

        return new Result<>(response);

    }

    @PostMapping("/course/save/{travelNoteId}")
    public void saveNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId,
            @RequestBody @Valid List<List<Long>> request) {
        travelNoteService.updateNoteCourse(travelNoteId, request);
    }

    @PostMapping("/title/change")
    public void changeNoteTitle(
            @RequestBody @Valid ChangeNoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(requestDto.getTravelNoteId(), requestDto.getNewTitle());
    }

    @PostMapping("/composition/change")
    public void changeNoteComposition(
            @RequestBody @Valid ChangeNoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(requestDto.getTravelNoteId(), requestDto.getAdult(), requestDto.getChild(), requestDto.getAnimal());
    }

    @PostMapping("/publicshare/change")
    public void changePublicShare(
            @RequestBody @Valid ChangePublicShareRequestDto requestDto) {
        travelNoteService.changePublicShare(requestDto.getTravelNoteId(), requestDto.isPublicShare());
    }

    @PostMapping("/companion/add")
    public void addCompanion(
            @RequestBody @Valid AddCompanionRequestDto requestDto) {
        travelNoteService.addNoteCompanion(requestDto.getTravelNoteId(), requestDto.getMemberId());
    }

}
