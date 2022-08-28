package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.travelnote.NoteDetailInfoResponseDto;
import com.yeoreodigm.server.dto.travelnote.TravelNoteIdDto;
import com.yeoreodigm.server.service.TravelNoteCommentService;
import com.yeoreodigm.server.service.TravelNoteService;
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

    private final TravelNoteCommentService travelNoteCommentService;

    @GetMapping("/{travelNoteId}")
    public NoteDetailInfoResponseDto callTravelNoteDetail(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        travelNoteService.updateTravelNoteLog(travelNote, member);

        return new NoteDetailInfoResponseDto(travelNoteService.getTravelNoteDetailInfo(travelNote));
    }

    @GetMapping("/comment/{travelNoteId}")
    public Result<List<CommentLikeDto>> callTravelNoteComment(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(travelNoteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member));
    }

    @PostMapping("/comment")
    public void addTravelNoteComment(
            @RequestBody @Valid ContentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteCommentService.addNoteComment(
                member,
                travelNoteService.getTravelNoteById(requestDto.getId()),
                requestDto.getContent());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteTravelNoteComment(
            @PathVariable(name = "commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteCommentService.deleteNoteComment(member, commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    public LikeItemDto callTravelNoteLike(
            @PathVariable("commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return travelNoteCommentService.getLikeInfo(commentId, member);
    }

    @PatchMapping("/comment/like")
    public void changeTravelNoteCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteCommentService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

    @PostMapping("/new")
    public TravelNoteIdDto makeMyTravelNote(
            @RequestBody HashMap<String, Long> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(request.get("travelNoteId"));

        return new TravelNoteIdDto(travelNoteService.createTravelNoteFromOther(travelNote, member));
    }

}
