package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.comment.CommentRequestDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.place.PlaceDetailDto;
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

    @GetMapping("/{placeId}")
    public PlaceDetailDto callPlaceDetailInfo(
            @PathVariable("placeId") Long placeId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Places place = placeService.getPlaceById(placeId);

        placeService.updateLog(place, member);
        return new PlaceDetailDto(place);
    }

    @GetMapping("/comment/{placeId}")
    public Result<List<CommentLikeDto>> callPlaceComment(
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

    @GetMapping("/comment/like/{placeCommentId}")
    public LikeItemDto callPlaceCommentLike(
            @PathVariable(name = "placeCommentId") Long placeCommentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return placeCommentService.getLikeInfo(placeCommentId, member);
    }

    @PatchMapping("/comment/like")
    public void changePlaceCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeCommentService.changeLike(member, requestDto.getId(), requestDto.isLike());
    }

}
