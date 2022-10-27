package com.yeoreodigm.server.controller;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place/detail")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    private final PlaceCommentService placeCommentService;

    private final MemberService memberService;

    @GetMapping("/{placeId}")
    public PlaceDetailDto callPlaceDetailInfo(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailDto(placeService.getPlaceById(placeId));
    }

    @GetMapping("/info/{placeId}")
    public PlaceExtraInfoDto callPlaceExtraInfo(
            @PathVariable("placeId") Long placeId) {
        return new PlaceExtraInfoDto(placeService.getPlaceExtraInfo(placeService.getPlaceById(placeId)));
    }

    @GetMapping("/comment/{placeId}")
    public Result<List<CommentLikeDto>> callPlaceComment(
            Authentication authentication,
            @PathVariable("placeId") Long placeId) {
        return new Result<>(placeCommentService.getPlaceCommentItems(
                placeService.getPlaceById(placeId),
                memberService.getMemberByAuthNullable(authentication)));
    }

    @PostMapping("/comment")
    public void addPlaceComment(
            Authentication authentication,
            @RequestBody @Valid CommentRequestDto requestDto) {
        placeCommentService.addPlaceComment(
                memberService.getMemberByAuthNullable(authentication),
                placeService.getPlaceById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deletePlaceComment(
            Authentication authentication,
            @PathVariable(name = "commentId") Long commentId) {
        placeCommentService.deletePlaceComment(memberService.getMemberByAuthNullable(authentication), commentId);
    }

    @GetMapping("/comment/like/{placeCommentId}")
    public LikeItemDto callPlaceCommentLike(
            Authentication authentication,
            @PathVariable(name = "placeCommentId") Long placeCommentId) {
        return placeCommentService.getLikeInfo(placeCommentId, memberService.getMemberByAuthNullable(authentication));
    }

    @PatchMapping("/comment/like")
    public void changePlaceCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        placeCommentService.changeLike(
                memberService.getMemberByAuthNullable(authentication), requestDto.getId(), requestDto.isLike());
    }

}
