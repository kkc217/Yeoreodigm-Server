package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.travelnote.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.TravelNoteIdDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/detail")
public class TravelNoteDetailApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteLogService travelNoteLogService;

    private final NoteCommentService noteCommentService;

    private final NoteCommentLikeService noteCommentLikeService;

    @GetMapping("/{travelNoteId}")
    public NoteDetailInfoResponseDto callTravelNoteDetail(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        travelNoteLogService.updateTravelNoteLog(travelNote, member);

        return new NoteDetailInfoResponseDto(travelNoteService.getTravelNoteDetailInfo(travelNote));
    }

    @GetMapping("/comment/{travelNoteId}")
    public Result<List<CommentItemDto>> callTravelNoteComment(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(noteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member));
    }

    @PostMapping("/comment")
    public void addTravelNoteComment(
            @RequestBody @Valid ContentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentService.addNoteComment(
                member,
                travelNoteService.getTravelNoteById(requestDto.getId()),
                requestDto.getContent());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteTravelNoteComment(
            @PathVariable(name = "commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentService.deleteNoteComment(member, commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    public LikeItemDto callTravelNoteLike(
            @PathVariable("commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return noteCommentLikeService.getLikeInfo(commentId, member);
    }

    @PatchMapping("/comment/like")
    public void changeTravelNoteCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        noteCommentLikeService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

    @PostMapping("/new")
    public TravelNoteIdDto makeMyTravelNote(
            @RequestBody HashMap<String, Long> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(request.get("travelNoteId"));

        TravelNote newTravelNote = travelNoteService.createTravelNoteFromOther(travelNote, member);
        Long newTravelNoteId = travelNoteService.submitFromOtherNote(travelNote, newTravelNote);

        return new TravelNoteIdDto(newTravelNoteId);
    }

//    @GetMapping("/course/{travelNoteId}/{day}")
//    public PageResult<NoteDetailCourseResponseDto> callTravelNoteDetailCourse(
//            @PathVariable("travelNoteId") Long travelNoteId,
//            @PathVariable("day") int day) {
//        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
//
//        Course course = courseService.getCourseByTravelNoteAndDay(travelNote, day);
//        RouteInfoDto routeInfo = courseService.getRouteInfoByCourse(course);
//
//        int next = courseService.checkNextCoursePage(
//                travelNote, day, 1);
//
//        return new PageResult<>(new NoteDetailCourseResponseDto(routeInfo, placeService.getPlacesByCourse(course)), next);
//    }

}
