package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.DetailPageConst;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.constraint.TravelNoteConst;
import com.yeoreodigm.server.dto.note.TravelNoteItemDto;
import com.yeoreodigm.server.dto.place.PlaceItemDto;
import com.yeoreodigm.server.dto.place.PlaceResponseDto;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.RecommendService;
import com.yeoreodigm.server.service.TravelNoteLikeService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendApiController {

    private final RecommendService recommendService;

    private final TravelNoteService travelNoteService;

    private final TravelNoteLikeService travelNoteLikeService;
    
    private final PlaceService placeService;

    @GetMapping("/place/{travelNoteId}")
    public Result<List<PlaceResponseDto>> getRecommendedPlacesFromNote(
            @PathVariable(name = "travelNoteId") Long travelNoteId) {
        List<Places> placeList = recommendService.getRecommendedPlacesByTravelNote(
                travelNoteService.getTravelNoteById(travelNoteId), TravelNoteConst.NUMBER_OF_RECOMMENDED_PLACES);

        return new Result<>(placeList.stream().map(PlaceResponseDto::new).toList());
    }

    @GetMapping("/place")
    public Result<List<PlaceItemDto>> getRecommendedPlaces(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(
                placeService.getRecommendedPlaces(MainPageConst.NUMBER_OF_RECOMMENDED_PLACES, member));
    }

    @GetMapping("/note")
    public Result<List<TravelNoteItemDto>> getRecommendedTravelNote(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(
                travelNoteService.getRecommendedNotes(MainPageConst.NUMBER_OF_RECOMMENDED_NOTES, member));
    }

    @GetMapping("/similar/note/{travelNoteId}")
    public Result<List<TravelNoteItemDto>> getSimilarTravelNote(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        List<TravelNote> travelNoteList = recommendService.getSimilarTravelNotes(
                travelNoteService.getTravelNoteById(travelNoteId),
                DetailPageConst.NUMBER_OF_SIMILAR_TRAVEL_NOTE,
                member);

        if (travelNoteList == null)
            travelNoteList = travelNoteService.getRandomNotes(DetailPageConst.NUMBER_OF_SIMILAR_TRAVEL_NOTE);

        return new Result<>(
                travelNoteList
                        .stream()
                        .map(travelNote -> new TravelNoteItemDto(
                                travelNote,
                                travelNoteLikeService.getLikeInfo(travelNote, member)))
                        .toList());
    }

}
