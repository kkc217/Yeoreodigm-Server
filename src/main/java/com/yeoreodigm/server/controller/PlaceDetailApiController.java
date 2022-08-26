package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.place.detail.PlaceCommentLikeRequestDto;
import com.yeoreodigm.server.dto.place.detail.CommentRequestDto;
import com.yeoreodigm.server.dto.place.detail.PlaceDetailResponseDto;
import com.yeoreodigm.server.service.PlaceCommentLikeService;
import com.yeoreodigm.server.service.PlaceCommentService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place/detail")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    private final PlaceCommentService placeCommentService;

    private final PlaceCommentLikeService placeCommentLikeService;

    @GetMapping("/{placeId}")
    public PlaceDetailResponseDto callPlaceDetailInfo(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailResponseDto(placeService.getPlaceById(placeId));
    }

    @GetMapping("/comment/{placeId}")
    public Result<List<CommentItemDto>> callPlaceComment(
            @PathVariable("placeId") Long placeId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(
                placeCommentService.getPlaceCommentItems(placeService.getPlaceById(placeId), member));
    }

    @PostMapping("/comment")
    public void addPlaceComment(
            @RequestBody @Valid CommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeCommentService.addPlaceComment(
                member,
                placeService.getPlaceById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deletePlaceComment(
            @PathVariable(name = "commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeCommentService.deletePlaceComment(member, commentId);
    }

    @PatchMapping("/comment/like")
    public void changePlaceCommentLike(
            @RequestBody @Valid PlaceCommentLikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeCommentLikeService.changeLike(member, requestDto.getCommentId(), requestDto.isLike());
    }

}
