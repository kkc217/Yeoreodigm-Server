package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.SearchPlacesResponseDto;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchApiController {

    private final PlaceService placeService;

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

        int next = placeService.checkNextSearchPage(content, page);

        return new PageResult<>(responseDtoList, next);
    }

}
