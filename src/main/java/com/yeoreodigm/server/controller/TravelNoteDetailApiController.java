package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.travelnote.NoteDetailInfoResponseDto;
import com.yeoreodigm.server.dto.travelnote.TravelNoteIdDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/detail")
public class TravelNoteDetailApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteCommentService travelNoteCommentService;

    private final CourseService courseService;

    private final PlaceService placeService;

    private final MemberService memberService;

    @GetMapping("/{travelNoteId}")
    public NoteDetailInfoResponseDto callTravelNoteDetail(
            Authentication authentication,
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new NoteDetailInfoResponseDto(
                memberService.getMemberByAuth(authentication),
                travelNoteService.getTravelNoteDetailInfo(travelNoteService.getTravelNoteById(travelNoteId)));
    }

    @GetMapping("/comment/{travelNoteId}")
    public Result<List<CommentLikeDto>> callTravelNoteComment(
            Authentication authentication,
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new Result<>(travelNoteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId),
                memberService.getMemberByAuth(authentication)));
    }

    @PostMapping("/comment")
    public void addTravelNoteComment(
            Authentication authentication,
            @RequestBody @Valid ContentRequestDto requestDto) {
        travelNoteCommentService.addNoteComment(
                memberService.getMemberByAuth(authentication),
                travelNoteService.getTravelNoteById(requestDto.getId()),
                requestDto.getContent());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteTravelNoteComment(
            Authentication authentication,
            @PathVariable(name = "commentId") Long commentId) {
        travelNoteCommentService.deleteNoteComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    public LikeItemDto callTravelNoteLike(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        return travelNoteCommentService.getLikeInfo(
                commentId, memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/comment/like")
    public void changeTravelNoteCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        travelNoteCommentService.changeTravelNoteCommentLike(
                memberService.getMemberByAuth(authentication), requestDto.getId(), requestDto.isLike());
    }

    @PostMapping("/new")
    public TravelNoteIdDto makeMyTravelNote(
            Authentication authentication,
            @RequestBody HashMap<String, Long> request) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(request.get("travelNoteId"));

        TravelNote newTravelNote = travelNoteService.createTravelNoteFromOther(
                travelNote, memberService.getMemberByAuth(authentication), placeService.getRandomImageUrl());

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
        for (Course course : courseList)
            courseService.saveNewCourse(newTravelNote, course.getDay(), course.getPlaces());

        return new TravelNoteIdDto(newTravelNote.getId());
    }

}
