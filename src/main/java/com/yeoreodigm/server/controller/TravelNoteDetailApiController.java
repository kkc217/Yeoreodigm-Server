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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/detail")
@Tag(name = "Travel Note Detail", description = "여행 노트 상세 페이지 API")
public class TravelNoteDetailApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteCommentService travelNoteCommentService;

    private final CourseService courseService;

    private final PlaceService placeService;

    private final MemberService memberService;

    @GetMapping("/{travelNoteId}")
    @Operation(summary = "기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public NoteDetailInfoResponseDto callTravelNoteDetail(
            Authentication authentication,
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new NoteDetailInfoResponseDto(
                memberService.getMemberByAuth(authentication),
                travelNoteService.getTravelNoteDetailInfo(travelNoteService.getTravelNoteById(travelNoteId)));
    }

    @GetMapping("/comment/{travelNoteId}")
    @Operation(summary = "댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<CommentLikeDto>> callTravelNoteComment(
            Authentication authentication,
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new Result<>(travelNoteCommentService.getNoteCommentInfo(
                travelNoteService.getTravelNoteById(travelNoteId),
                memberService.getMemberByAuth(authentication)));
    }

    @PostMapping("/comment")
    @Operation(summary = "댓글 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void addTravelNoteComment(
            Authentication authentication,
            @RequestBody @Valid ContentRequestDto requestDto) {
        travelNoteCommentService.addNoteComment(
                memberService.getMemberByAuth(authentication),
                travelNoteService.getTravelNoteById(requestDto.getId()),
                requestDto.getContent());
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteTravelNoteComment(
            Authentication authentication,
            @PathVariable(name = "commentId") Long commentId) {
        travelNoteCommentService.deleteNoteComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    @Operation(summary = "댓글 좋아요 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public LikeItemDto callTravelNoteLike(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        return travelNoteCommentService.getLikeInfo(
                commentId, memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/comment/like")
    @Operation(summary = "댓글 좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeTravelNoteCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        travelNoteCommentService.changeTravelNoteCommentLike(
                memberService.getMemberByAuth(authentication), requestDto.getId(), requestDto.isLike());
    }

    @PostMapping("/new")
    @Operation(summary = "이 일정으로 계획 만들기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
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
