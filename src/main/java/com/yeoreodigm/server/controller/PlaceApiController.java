package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.place.PlaceCoordinateDto;
import com.yeoreodigm.server.dto.place.PlaceDetailDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.dto.place.PlaceStringIdDto;
import com.yeoreodigm.server.dto.restaurant.RestaurantRouteDto;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.RestaurantService;
import com.yeoreodigm.server.service.RouteInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.SearchConst.SEARCH_OPTION_LIKE_DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place")
@Tag(name = "Place", description = "여행지 API")
public class PlaceApiController {

    private final PlaceService placeService;

    private final MemberService memberService;

    private final RestaurantService restaurantService;

    private final RouteInfoService routeInfoService;

    @GetMapping("/like/list/{page}/{limit}")
    @Operation(summary = "좋아요 누른 여행지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PlaceLikeDto>> callPlaceLikeList(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuth(authentication);

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

    @GetMapping("/like/list")
    @Operation(summary = "좋아요 누른 여행지 조회 v2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PlaceLikeDto>> callPlaceLikeListV2(
            Authentication authentication,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option) {
        Member member = memberService.getMemberByAuth(authentication);

        if (Objects.equals(SEARCH_OPTION_LIKE_DESC, option)) {
            return new PageResult<>(
                    placeService.getPlacesOrderByLike(member, page, limit)
                            .stream()
                            .map(place -> new PlaceLikeDto(place, placeService.getLikeInfo(place, member)))
                            .toList(),
                    placeService.checkNextPlaceLikePage(member, page, limit));
        }

        return new PageResult<>(
                placeService.getPlacesByPlaceLikes(placeService.getPlaceLikesByMemberPaging(member, page, limit))
                        .stream()
                        .map(place -> new PlaceLikeDto(place, placeService.getLikeInfo(place, member)))
                        .toList(),
                placeService.checkNextPlaceLikePage(member, page, limit));
    }

    @GetMapping("/like/list/{memberId}/{page}/{limit}")
    @Operation(summary = "좋아요 누른 여행지 조회 (멤버 상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PlaceLikeDto>> callMemberPlaceLikeList(
            Authentication authentication,
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
            LikeItemDto likeInfo
                    = placeService.getLikeInfo(place, memberService.getMemberByAuth(authentication));
            response.add(new PlaceLikeDto(place, likeInfo));
        }

        return new PageResult<>(response, next);
    }

    @GetMapping("/like/{placeId}")
    @Operation(summary = "좋아요 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public LikeItemDto callPlaceLikeInfo(
            Authentication authentication,
            @PathVariable("placeId") Long placeId) {
        return placeService.getLikeInfo(
                placeService.getPlaceById(placeId),
                memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/like")
    @Operation(summary = "좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changePlaceLike(
            Authentication authentication,
            @RequestBody LikeRequestDto requestDto) {
        placeService.changePlaceLike(
                memberService.getMemberByAuth(authentication), requestDto.getId(), requestDto.isLike());
    }

    @GetMapping("/popular")
    @Operation(summary = "최근 많이 방문한 여행지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<PlaceCoordinateDto>> callPopularPlaces() {
        return new Result<>(placeService.getPopularPlaces(MainPageConst.NUMBER_OF_POPULAR_PLACES));
    }

    @GetMapping("/all")
    @Operation(summary = "모든 여행지 ID 가져오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<PlaceStringIdDto>> callAllPlaceId() {
        return new Result<>(placeService.getAllPlaceStringIdDto());
    }

    @GetMapping("/restaurant")
    @Operation(summary = "근처 음식점 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<RestaurantRouteDto>> callNearRestaurant(
            @RequestParam("placeId") Long placeId,
            @RequestParam(value = "type", required = false, defaultValue = "0") int type,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        Places place = placeService.getPlaceById(placeId);
        List<Long> restaurantIdList = restaurantService.getNearRestaurantId(placeId, type);

        List<Restaurant> restaurantList = restaurantService.getRestaurantsPaging(restaurantIdList, page, limit);

        List<RestaurantRouteDto> response = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            RestaurantRouteInfo restaurantRouteInfo = routeInfoService.getRestaurantRouteInfo(place, restaurant);

            if (Objects.isNull(restaurantRouteInfo)) {
                restaurantRouteInfo = routeInfoService.updateRestaurantRouteInfo(place, restaurant);
            }

            response.add(new RestaurantRouteDto(
                    restaurant, routeInfoService.getRouteDataRestaurant(restaurantRouteInfo)));
        }

        return new PageResult<>(
                response,
                restaurantService.checkNextRestaurants(restaurantIdList, page, limit));
    }

    @GetMapping("/{placeId}")
    @Operation(summary = "여행지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PlaceDetailDto callPlaceBoard(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailDto(null, placeService.getPlaceById(placeId));
    }

}
