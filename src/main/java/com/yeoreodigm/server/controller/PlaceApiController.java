package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.place.PlaceResponseDto;
import com.yeoreodigm.server.service.PlaceLikeService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {

    private final PlaceService placeService;

    private final PlaceLikeService placeLikeService;

    @GetMapping("/like/list/{page}")
    public PageResult<List<PlaceResponseDto>> callPlaceLikeList(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page) {
        List<PlaceLike> placeLikeList
                = placeLikeService.getPlaceLikesByMemberPaging(member, page, QueryConst.PLACE_LIKE_PAGING_LIMIT);

        List<Places> placeList = placeService.getPlacesByPlaceLikes(placeLikeList);

        int next = placeLikeService.checkNextPlaceLikePage(member, page, QueryConst.PLACE_LIKE_PAGING_LIMIT);

        return new PageResult<>(placeList.stream().map(PlaceResponseDto::new).toList(), next);
    }

    @GetMapping("/like/{placeId}")
    public LikeItemDto callPlaceLikeItem(
            @PathVariable("placeId") Long placeId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return placeLikeService.getLikeInfo(placeService.getPlaceById(placeId), member);
    }
}
