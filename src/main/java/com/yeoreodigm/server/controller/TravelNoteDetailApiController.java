package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.DetailPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.travelnote.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.CourseCoordinateDto;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteIdResponseDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/detail")
public class TravelNoteDetailApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteLikeService travelNoteLikeService;

    private final TravelNoteLogService travelNoteLogService;

    private final NoteCommentService noteCommentService;

    private final NoteCommentLikeService noteCommentLikeService;

    private final CourseService courseService;

    private final MapMarkerService mapMarkerService;

    private final PlaceService placeService;

    private final RecommendService recommendService;

    @GetMapping("/{travelNoteId}")
    public NoteDetailInfoResponseDto callTravelNoteDetail(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        travelNoteLogService.updateTravelNoteLog(travelNote, member);

        return new NoteDetailInfoResponseDto(travelNoteService.getTravelNoteDetailInfo(travelNote));
    }

    @GetMapping("/like/{travelNoteId}")
    public LikeItemDto callTravelNoteLike(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return travelNoteLikeService.getLikeInfo(travelNoteService.getTravelNoteById(travelNoteId), member);
    }

    @PostMapping("/like")
    public void changeTravelNoteLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteLikeService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

    @GetMapping("/course/{travelNoteId}/{day}")
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

    @GetMapping("/comment/{travelNoteId}")
    public Result<List<CommentItemDto>> callTravelNoteComment(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(noteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member));
    }

    @PostMapping("/comment/add")
    public CommentItemDto addTravelNoteComment(
            @RequestBody @Valid NoteCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return noteCommentService.addNoteComment(
                member,
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getText());
    }

    @PostMapping("/comment/delete")
    public void deleteTravelNoteComment(
            @RequestBody @Valid NoteCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentService.deleteNoteComment(member, requestDto.getCommentId());
    }

    @PostMapping("/comment/like")
    public void changeTravelNoteCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentLikeService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

    @PostMapping("/make")
    public TravelNoteIdResponseDto makeMyTravelNote(
            @RequestBody @Valid TravelNoteRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(requestDto.getTravelNoteId());

        TravelNote newTravelNote = travelNoteService.createTravelNoteFromOther(travelNote, member);
        Long newTravelNoteId = travelNoteService.submitFromOtherNote(travelNote, newTravelNote);

        return new TravelNoteIdResponseDto(newTravelNoteId);
    }

}
