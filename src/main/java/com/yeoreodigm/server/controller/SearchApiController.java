package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.member.MemberEmailItemDto;
import com.yeoreodigm.server.dto.place.PlaceCoordinateDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.dto.search.RelatedSearchDto;
import com.yeoreodigm.server.dto.travelnote.PublicTravelNoteDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yeoreodigm.server.dto.constraint.SearchConst.*;

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
                        .searchPlaces(content, page, QueryConst.SEARCH_PAGING_LIMIT, SEARCH_OPTION_DEFAULT)
                        .stream()
                        .map(PlaceCoordinateDto::new)
                        .toList();

        int next = placeService.checkNextSearchPage(
                content, page, QueryConst.SEARCH_PAGING_LIMIT, SEARCH_OPTION_DEFAULT);

        return new PageResult<>(responseDtoList, next);
    }

    @GetMapping("/member/{content}")
    public MemberEmailItemDto searchMember(
            @PathVariable("content") String content) {
        Member member = memberService.searchMember(content);
        if (member != null) {
            return new MemberEmailItemDto(member);
        } else {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }
    }

    @GetMapping("")
    public RelatedSearchDto relatedSearch(
            @RequestParam("content") String content) {
        List<Places> placeList = placeService.searchPlaces(
                content, 1, NUMBER_OF_RELATED_PLACES, SEARCH_OPTION_DEFAULT);
        List<TravelNote> travelNoteList = travelNoteService.searchTravelNote(
                content, 1, NUMBER_OF_RELATED_TRAVELNOTES, SEARCH_OPTION_DEFAULT);
        List<Member> memberList = memberService.searchMembersByNickname(content, 1, NUMBER_OF_RELATED_MEMBERS);

        if (NUMBER_OF_RELATED_TRAVELNOTES > travelNoteList.size()) {
            placeList.addAll(
                    placeService.searchPlaces(
                            content,
                            2,
                            NUMBER_OF_RELATED_TRAVELNOTES - travelNoteList.size(),
                            SEARCH_OPTION_DEFAULT));
        } else if (NUMBER_OF_RELATED_PLACES > placeList.size()) {
            travelNoteList.addAll(
                    travelNoteService.searchTravelNote(content,
                            2,
                            NUMBER_OF_RELATED_PLACES - placeList.size(),
                            SEARCH_OPTION_DEFAULT));
        }

        return new RelatedSearchDto(placeList, travelNoteList, memberList);
    }

    @GetMapping("/place")
    public PageResult<List<PlaceLikeDto>> searchPlaces(
            Authentication authentication,
            @RequestParam("content") String content,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option) {
        return new PageResult<>(
                placeService.searchPlaces(content, page, limit, option)
                        .stream()
                        .map(place -> new PlaceLikeDto(
                                place,
                                placeService.getLikeInfo(
                                        place, memberService.getMemberByAuthNullable(authentication))))
                        .toList(),
                placeService.checkNextSearchPage(content, page, limit, option));
    }

    @GetMapping("/note")
    public PageResult<List<PublicTravelNoteDto>> searchTravelNotes(
            Authentication authentication,
            @RequestParam("content") String content,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option) {
        return new PageResult<>(
                travelNoteService.searchTravelNote(content, page, limit, option)
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(
                                travelNote, memberService.getMemberByAuthNullable(authentication)))
                        .toList(),
                travelNoteService.checkNextSearchTravelNote(content, page, limit, option));
    }

    @GetMapping("/member")
    public PageResult<List<MemberEmailItemDto>> searchMembers(
            @RequestParam("content") String content,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        return new PageResult<>(
                memberService.searchMembersByNickname(content, page, limit)
                        .stream()
                        .map(MemberEmailItemDto::new)
                        .toList(),
                memberService.checkNextSearchMembersByNickname(content, page, limit));
    }

}
