package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.place.PlaceCommentLikeRequestDto;
import com.yeoreodigm.server.dto.detail.place.PlaceCommentRequestDto;
import com.yeoreodigm.server.dto.detail.place.PlaceDetailResponseDto;
import com.yeoreodigm.server.dto.detail.place.PlaceLikeRequestDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.service.PlaceCommentLikeService;
import com.yeoreodigm.server.service.PlaceCommentService;
import com.yeoreodigm.server.service.PlaceLikeService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail/place")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    private final PlaceLikeService placeLikeService;

    private final PlaceCommentService placeCommentService;

    private final PlaceCommentLikeService placeCommentLikeService;

    @GetMapping("/{placeId}")
    public PlaceDetailResponseDto callPlaceDetail(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailResponseDto(placeService.getPlaceById(placeId));
    }

    @PatchMapping("/like")
    public void changePlaceLike(
            @RequestBody @Valid PlaceLikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeLikeService.changePlaceLike(member, requestDto.getPlaceId(), requestDto.isLike());
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
            @RequestBody @Valid PlaceCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeCommentService.addPlaceComment(
                member,
                placeService.getPlaceById(requestDto.getPlaceId()),
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
