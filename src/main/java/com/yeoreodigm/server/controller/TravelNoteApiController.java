package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.note.CallNoteCourseRequestDto;
import com.yeoreodigm.server.dto.note.CallNoteCourseResponseDto;
import com.yeoreodigm.server.dto.note.CallNoteInfoResponseDto;
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

    @PostMapping("/course/{page}")
    public PageResult<List<CallNoteCourseResponseDto>> callNoteCourse(
            @PathVariable("page") int page,
            @RequestBody @Valid CallNoteCourseRequestDto requestDto) {

        List<Course> courseList = courseService.searchCourse(
                requestDto.getTravelNoteId(), page, QueryConst.PAGING_LIMIT_PUBLIC);

        List<CallNoteCourseResponseDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CallNoteCourseResponseDto(course.getDay(), placeService.searchPlacesByCourse(course)));
        }

        int next = courseService.checkNextCoursePage(
                requestDto.getTravelNoteId(), page, QueryConst.PAGING_LIMIT_PUBLIC);

        if (requestDto.getNoteAuthority() != NoteAuthority.ROLE_VISITOR) {
            return new PageResult<>(response, next);
        } else {
            //(수정하기) 방문자인 경우 - 댓글 없이 전달
            return new PageResult<>(response, next);
        }
    }

}
