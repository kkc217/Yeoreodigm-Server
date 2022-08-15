package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.place.PlaceDetailResponseDto;
import com.yeoreodigm.server.dto.detail.place.PlaceLikeRequestDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.service.PlaceLikeService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail/place")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    private final PlaceLikeService placeLikeService;

    @GetMapping("/{placeId}")
    public PlaceDetailResponseDto callPlaceDetail(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailResponseDto(placeService.getPlaceById(placeId));
    }

    @GetMapping("/like/{placeId}")
    public LikeItemDto callPlaceLikeInfo(
            @PathVariable("placeId") Long placeId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return placeLikeService.getLikeInfo(placeService.getPlaceById(placeId), member);
    }

    @PatchMapping("/like")
    public void changePlaceLike(
            @RequestBody @Valid PlaceLikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeLikeService.changePlaceLike(member, requestDto.getPlaceId(), requestDto.isLike());
    }

}
