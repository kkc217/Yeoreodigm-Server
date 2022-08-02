package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.search.SearchPlacesResponseDto;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.search.SearchMemberResponseDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchApiController {

    private final PlaceService placeService;

    private final MemberService memberService;

    @GetMapping("/place/{content}/{page}")
    public PageResult<List<SearchPlacesResponseDto>> searchPlaces(
            @PathVariable("content") String content,
            @PathVariable("page") int page) {
        List<SearchPlacesResponseDto> responseDtoList =
                placeService
                        .searchPlaces(content, page, QueryConst.PAGING_LIMIT_PUBLIC)
                        .stream()
                        .map(SearchPlacesResponseDto::new)
                        .toList();

        int next = placeService.checkNextSearchPage(content, page, QueryConst.PAGING_LIMIT_PUBLIC);

        return new PageResult<>(responseDtoList, next);
    }

    @GetMapping("/member/{content}")
    public SearchMemberResponseDto searchMember(
            @PathVariable("content") String content) {

        Member member = memberService.searchMember(content);
        if (member != null) {
            return new SearchMemberResponseDto(member.getProfileImage(), member.getNickname(), member.getEmail());
        } else {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }

    }

}
