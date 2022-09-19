package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.member.MemberItemDto;
import com.yeoreodigm.server.dto.place.PlaceCoordinateDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.dto.search.RelatedSearchDto;
import com.yeoreodigm.server.dto.travelnote.PublicTravelNoteDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.SearchConst.NUMBER_OF_RELATED_PLACES;
import static com.yeoreodigm.server.dto.constraint.SearchConst.NUMBER_OF_RELATED_TRAVELNOTES;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchApiController {

    private final PlaceService placeService;

    private final TravelNoteService travelNoteService;

    private final MemberService memberService;

    @GetMapping("/place/{content}/{page}")
    public PageResult<List<PlaceCoordinateDto>> searchPlaceCoordinates(
            @PathVariable("content") String content,
            @PathVariable("page") int page) {
        List<PlaceCoordinateDto> responseDtoList =
                placeService
                        .searchPlaces(content, page, QueryConst.SEARCH_PAGING_LIMit)
                        .stream()
                        .map(PlaceCoordinateDto::new)
                        .toList();

        int next = placeService.checkNextSearchPage(content, page, QueryConst.SEARCH_PAGING_LIMit);

        return new PageResult<>(responseDtoList, next);
    }

    @GetMapping("/member/{content}")
    public MemberItemDto searchMember(
            @PathVariable("content") String content) {
        Member member = memberService.searchMember(content);
        if (member != null) {
            return new MemberItemDto(member);
        } else {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }
    }

    @GetMapping("/{content}")
    public RelatedSearchDto relatedSearch(
            @PathVariable("content") String content) {
        List<Places> placeList = placeService.searchPlaces(content, 1, NUMBER_OF_RELATED_PLACES);
        List<TravelNote> travelNoteList
                = travelNoteService.searchTravelNote(content, 1, NUMBER_OF_RELATED_TRAVELNOTES);

        if (NUMBER_OF_RELATED_TRAVELNOTES > travelNoteList.size()) {
            placeList.addAll(
                    placeService.searchPlaces(
                            content,
                            2,
                            NUMBER_OF_RELATED_TRAVELNOTES - travelNoteList.size()));
        } else if (NUMBER_OF_RELATED_PLACES > placeList.size()) {
            travelNoteList.addAll(
                    travelNoteService.searchTravelNote(content,
                            2,
                            NUMBER_OF_RELATED_PLACES - placeList.size()));
        }

        return new RelatedSearchDto(placeList, travelNoteList);
    }

    @GetMapping("/place/{content}/{page}/{limit}")
    public PageResult<List<PlaceLikeDto>> searchPlaces(
            @PathVariable("content") String content,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new PageResult<>(
                placeService.searchPlaces(content, page, limit)
                        .stream()
                        .map(place -> new PlaceLikeDto(
                                place, placeService.getLikeInfo(place, member)))
                        .toList(),
                placeService.checkNextSearchPage(content, page, limit));
    }

    @GetMapping("/note/{content}/{page}/{limit}")
    public PageResult<List<PublicTravelNoteDto>> searchTravelNotes(
            @PathVariable("content") String content,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new PageResult<>(
                travelNoteService.searchTravelNote(content, page, limit)
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(travelNote, member))
                        .toList(),
                travelNoteService.checkNextSearchTravelNote(content, page, limit));
    }

}
