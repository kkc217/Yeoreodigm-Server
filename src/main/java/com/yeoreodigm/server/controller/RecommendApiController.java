package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.DetailPageConst;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.constraint.TravelNoteConst;
import com.yeoreodigm.server.dto.place.PlaceCoordinateDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.dto.travelnote.TravelNoteLikeDto;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.RecommendService;
import com.yeoreodigm.server.service.TravelNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.MainPageConst.NUMBER_OF_RECOMMENDED_PLACES;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
@Tag(name = "Recommend", description = "추천 API")
public class RecommendApiController {

    private final RecommendService recommendService;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    @GetMapping("/place/{travelNoteId}")
    @Operation(summary = "추천 여행지 조회 (여행 메이킹 노트)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<PlaceCoordinateDto>> getRecommendedPlacesFromNote(
            @PathVariable(name = "travelNoteId") Long travelNoteId) {
        List<Places> placeList = recommendService.getRecommendedPlacesByTravelNote(
                travelNoteService.getTravelNoteById(travelNoteId), TravelNoteConst.NUMBER_OF_RECOMMENDED_PLACES);

        return new Result<>(placeList.stream().map(PlaceCoordinateDto::new).toList());
    }

    @GetMapping("/place")
    @Operation(summary = "추천 여행지 조회 (메인 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<PlaceLikeDto>> getRecommendedPlaces(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null)
            return new Result<>(
                    placeService.getPlaceLikeDtoList(
                            recommendService.getRecommendedPlaces(
                                    List.of(member), new ArrayList<>(), NUMBER_OF_RECOMMENDED_PLACES), member));

        return new Result<>(
                placeService.getPlaceLikeDtoList(
                        placeService.getRandomPlaces(NUMBER_OF_RECOMMENDED_PLACES), null));
    }

    @GetMapping("/note")
    @Operation(summary = "추천 여행 노트 조회 (메인 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<TravelNoteLikeDto>> getRecommendedTravelNote(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        List<TravelNote> travelNoteList
                = recommendService.getRecommendedNotes(MainPageConst.NUMBER_OF_RECOMMENDED_NOTES, member);

        return new Result<>(travelNoteService.getTravelNoteItemList(travelNoteList, member));
    }

    @GetMapping("/similar/note/{travelNoteId}")
    @Operation(summary = "추천 여행 노트 조회 (상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<TravelNoteLikeDto>> getSimilarTravelNote(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        List<TravelNote> travelNoteList = recommendService.getSimilarTravelNotes(
                travelNoteService.getTravelNoteById(travelNoteId),
                DetailPageConst.NUMBER_OF_SIMILAR_TRAVEL_NOTE,
                member);

        return new Result<>(
                travelNoteList
                        .stream()
                        .map(travelNote -> new TravelNoteLikeDto(
                                travelNote,
                                travelNoteService.getLikeInfo(travelNote, member)))
                        .toList());
    }

}
