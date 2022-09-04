package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteAuthority;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.comment.CourseCommentRequestDto;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.member.MemberItemDto;
import com.yeoreodigm.server.dto.travelnote.*;
import com.yeoreodigm.server.service.CourseCommentService;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final CourseCommentService commentService;

    private final MemberService memberService;

    @PostMapping("/new")
    public TravelNoteIdDto createNewTravelNote(
            @RequestBody @Valid NewTravelNoteRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new TravelNoteIdDto(travelNoteService.createTravelNote(member, requestDto));
    }

    @GetMapping("/{travelNoteId}")
    public TravelNoteInfoDto callTravelMakingNoteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(member, travelNote);

        return new TravelNoteInfoDto(noteAuthority, travelNote);
    }

    @PatchMapping("/title")
    public void changeTravelMakingNoteTitle(
            @RequestBody @Valid NoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.getNewTitle());
    }

    @PatchMapping("/composition")
    public void changeTravelMakingNoteComposition(
            @RequestBody @Valid NoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getAdult(),
                requestDto.getChild(),
                requestDto.getAnimal());
    }

    @PatchMapping("/public-share")
    public void changeTravelMakingNotePublicShare(
            @RequestBody @Valid PublicShareRequestDto requestDto) {
        travelNoteService.changePublicShare(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.isPublicShare());
    }

    @GetMapping("/companion/{travelNoteId}")
    public Result<List<MemberItemDto>> callTravelMakingNoteCompanion(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Member> memberList = travelNoteService.getCompanionMember(travelNote);
        List<MemberItemDto> response = memberList
                .stream()
                .map(MemberItemDto::new)
                .toList();

        return new Result<>(response);
    }

    @PatchMapping("/companion")
    public void addTravelMakingNoteCompanion(
            @RequestBody @Valid ContentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.addNoteCompanion(
                travelNoteService.getTravelNoteById(requestDto.getId()),
                member,
                memberService.searchMember(requestDto.getContent()));
    }

    @DeleteMapping("/companion/{travelNoteId}/{memberId}")
    public void deleteTravelMakingNoteCompanion(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "memberId") Long memberId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.deleteCompanion(
                travelNoteService.getTravelNoteById(travelNoteId), member, memberId);
    }

    @GetMapping("/comment/{travelNoteId}/{day}")
    public Result<List<CommentItemDto>> callCourseComment(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "day") int day) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<CourseComment> courseCommentList
                = commentService.getCourseCommentsByTravelNoteAndDay(travelNote, day);

        return new Result<>(
                courseCommentList
                        .stream()
                        .map(CommentItemDto::new)
                        .toList());
    }

    @PostMapping("/comment")
    public void addCourseComment(
            @RequestBody @Valid CourseCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        commentService.addCourseComment(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                member,
                requestDto.getDay(),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteCourseComment(
            @PathVariable(name = "commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        commentService.deleteCourseComment(member, commentId);
    }

    @GetMapping("/week")
    public Result<List<TravelNoteItemDto>> callWeekTravelNote(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(travelNoteService.getWeekNotes(MainPageConst.NUMBER_OF_WEEK_NOTES, member));
    }

    @GetMapping("/like/{travelNoteId}")
    public LikeItemDto callTravelNoteLike(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return travelNoteService.getLikeInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member);
    }

    @PatchMapping("/like")
    public void changeTravelNoteLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

}
