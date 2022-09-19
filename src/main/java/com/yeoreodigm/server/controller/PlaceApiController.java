package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.place.PlaceCoordinateDto;
import com.yeoreodigm.server.dto.place.PlaceStringIdDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {

    private final PlaceService placeService;

    private final MemberService memberService;

    @GetMapping("/like/list/{page}/{limit}")
    public PageResult<List<PlaceLikeDto>> callPlaceLikeList(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        List<PlaceLike> placeLikeList
                = placeService.getPlaceLikesByMemberPaging(member, page, limit);

        List<Places> placeList = placeService.getPlacesByPlaceLikes(placeLikeList);

        int next = placeService.checkNextPlaceLikePage(member, page, limit);

        List<PlaceLikeDto> response = new ArrayList<>();
        for (Places place : placeList) {
            LikeItemDto likeInfo = placeService.getLikeInfo(place, member);
            response.add(new PlaceLikeDto(place, likeInfo));
        }

        return new PageResult<>(response, next);
    }

    @GetMapping("/like/list/{memberId}/{page}/{limit}")
    public PageResult<List<PlaceLikeDto>> callMemberPlaceLikeList(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("memberId") Long memberId,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member targetMember = memberService.getMemberById(memberId);

        List<PlaceLike> placeLikeList
                = placeService.getPlaceLikesByMemberPaging(targetMember, page, limit);

        List<Places> placeList = placeService.getPlacesByPlaceLikes(placeLikeList);

        int next = placeService.checkNextPlaceLikePage(targetMember, page, limit);

        List<PlaceLikeDto> response = new ArrayList<>();
        for (Places place : placeList) {
            LikeItemDto likeInfo = placeService.getLikeInfo(place, member);
            response.add(new PlaceLikeDto(place, likeInfo));
        }

        return new PageResult<>(response, next);
    }

    @GetMapping("/like/{placeId}")
    public LikeItemDto callPlaceLikeInfo(
            @PathVariable("placeId") Long placeId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return placeService.getLikeInfo(placeService.getPlaceById(placeId), member);
    }

    @PatchMapping("/like")
    public void changePlaceLike(
            @RequestBody LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        placeService.changePlaceLike(member, requestDto.getId(), requestDto.isLike());
    }

    @GetMapping("/popular")
    public Result<List<PlaceCoordinateDto>> callPopularPlaces() {
        return new Result<>(placeService.getPopularPlaces(MainPageConst.NUMBER_OF_POPULAR_PLACES)
                .stream()
                .map(PlaceCoordinateDto::new)
                .toList());
    }

    @GetMapping("/all")
    public Result<List<PlaceStringIdDto>> callAllPlaceId() {
        return new Result<>(placeService.getAll()
                .stream()
                .map(PlaceStringIdDto::new)
                .toList());
    }

}
