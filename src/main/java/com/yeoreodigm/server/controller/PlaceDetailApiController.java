package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.comment.CommentRequestDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.place.PlaceDetailDto;
import com.yeoreodigm.server.dto.place.PlaceExtraInfoDto;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceCommentService;
import com.yeoreodigm.server.service.PlaceService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place/detail")
@Tag(name = "Place Detail", description = "여행지 상세 페이지 API")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    private final PlaceCommentService placeCommentService;

    private final MemberService memberService;

    @GetMapping("/{placeId}")
    @Operation(summary = "기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PlaceDetailDto callPlaceDetailInfo(
            Authentication authentication,
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailDto(
                memberService.getMemberByAuth(authentication), placeService.getPlaceById(placeId));
    }

    @GetMapping("")
    @Operation(summary = "기본 정보 조회 v2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PlaceDetailDto callPlaceDetailInfo(
            Authentication authentication,
            @RequestParam(name = "placeId") Long placeId,
            @RequestParam(name = "option", required = false, defaultValue = "KOR") String option
    ) {
        return placeService.getPlaceDetailDto(
                memberService.getMemberByAuth(authentication), placeService.getPlaceById(placeId), option);
    }

    @GetMapping("/info/{placeId}")
    @Operation(summary = "추가 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PlaceExtraInfoDto callPlaceExtraInfo(
            @PathVariable("placeId") Long placeId) {
        return new PlaceExtraInfoDto(placeService.getPlaceExtraInfo(placeService.getPlaceById(placeId)));
    }

    @GetMapping("/comment/{placeId}")
    @Operation(summary = "댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<CommentLikeDto>> callPlaceComment(
            Authentication authentication,
            @PathVariable("placeId") Long placeId) {
        return new Result<>(placeCommentService.getPlaceCommentItems(
                placeService.getPlaceById(placeId),
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
    public void addPlaceComment(
            Authentication authentication,
            @RequestBody @Valid CommentRequestDto requestDto) {
        placeCommentService.addPlaceComment(
                memberService.getMemberByAuth(authentication),
                placeService.getPlaceById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deletePlaceComment(
            Authentication authentication,
            @PathVariable(name = "commentId") Long commentId) {
        placeCommentService.deletePlaceComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/comment/like/{placeCommentId}")
    @Operation(summary = "댓글 좋아요 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public LikeItemDto callPlaceCommentLike(
            Authentication authentication,
            @PathVariable(name = "placeCommentId") Long placeCommentId) {
        return placeCommentService.getLikeInfo(placeCommentId, memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/comment/like")
    @Operation(summary = "댓글 좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changePlaceCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        placeCommentService.changeLike(
                memberService.getMemberByAuth(authentication), requestDto.getId(), requestDto.isLike());
    }

}
