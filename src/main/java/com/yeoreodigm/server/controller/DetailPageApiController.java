package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.DetailPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.CourseCoordinateDto;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteResponseDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail")
public class DetailPageApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteLikeService travelNoteLikeService;

    private final TravelNoteLogService travelNoteLogService;

    private final CourseService courseService;

    private final NoteCommentService noteCommentService;

    private final NoteCommentLikeService noteCommentLikeService;

    private final MapMarkerService mapMarkerService;

    private final PlaceService placeService;

    @GetMapping("/travelnote/{travelNoteId}")
    public NoteDetailResponseDto callTravelNoteDetail(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        TravelNoteDetailInfo travelNoteInfo = travelNoteService.getTravelNoteDetailInfo(travelNote);

        LikeItemDto travelNoteLikeInfo = travelNoteLikeService.getLikeInfo(travelNote, member);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
        List<String> markerColorList = mapMarkerService.getMarkerColors(courseList.size());
        List<CourseCoordinateDto> coordinateDtoList = courseList.stream().map(course -> new CourseCoordinateDto(
                course.getDay(),
                markerColorList.get(course.getDay() - 1),
                placeService.getPlacesByCourse(course))).collect(Collectors.toList());

        //여행 노트 추천 - AI API 구현시 수정 예정
        List<TravelNote> recommendedNoteList = travelNoteService.getTempTravelNoteList(4, member);

        List<CommentItemDto> commentList = noteCommentService.getNoteCommentInfo(travelNote, member);

        if (member != null)
            travelNoteLogService.updateTravelNoteLog(travelNote, member);

        return new NoteDetailResponseDto(
                travelNoteInfo, travelNoteLikeInfo, coordinateDtoList, recommendedNoteList, commentList);
    }

    @PostMapping("/travelnote/like")
    public void changeTravelNoteLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteLikeService.changeTravelNoteLike(member, requestDto.getTravelNoteId(), requestDto.isLike());
    }

    @GetMapping("/travelnote/course/{travelNoteId}/{day}")
    public PageResult<NoteDetailCourseResponseDto> callTravelNoteDetailCourse(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("day") int day) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        Course course = courseService.getCourseByTravelNoteAndDay(travelNote, day);
        RouteInfoDto routeInfo = courseService.getRouteInfoByCourse(course);

        int next = courseService.checkNextCoursePage(
                travelNote, day, 1);

        return new PageResult<>(new NoteDetailCourseResponseDto(routeInfo, placeService.getPlacesByCourse(course)), next);
    }

    @GetMapping("/travelnote/comment/{travelNoteId}")
    public Result<List<CommentItemDto>> callTravelNoteComment(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(noteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member));
    }

    @PostMapping("/travelnote/comment/add")
    public CommentItemDto addTravelNoteComment(
            @RequestBody @Valid NoteCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return noteCommentService.addNoteComment(
                member,
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getText());
    }

    @PostMapping("/travelnote/comment/delete")
    public void deleteTravelNoteComment(
            @RequestBody @Valid NoteCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentService.deleteNoteComment(member, requestDto.getCommentId());
    }

    @PostMapping("/travelnote/comment/like")
    public void changeTravelNoteCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentLikeService.changeTravelNoteLike(member, requestDto.getCommentId(), requestDto.isLike());
    }

    @PostMapping("/travelnote/make")
    public TravelNoteResponseDto makeMyTravelNote(
            @RequestBody @Valid TravelNoteRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(requestDto.getTravelNoteId());

        TravelNote newTravelNote = travelNoteService.createTravelNoteFromOther(travelNote, member);
        Long newTravelNoteId = travelNoteService.submitFromOtherNote(travelNote, newTravelNote);

        return new TravelNoteResponseDto(newTravelNoteId);
    }

}
