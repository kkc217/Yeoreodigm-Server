package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.SearchPlacesResponseDto;
import com.yeoreodigm.server.dto.search.SearchRequestDto;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchApiController {

    private final PlaceService placeService;

    @GetMapping("/place/{page}")
    public PageResult<List<SearchPlacesResponseDto>> searchPlaces(
            @RequestBody @Valid SearchRequestDto searchRequestDto,
            @PathVariable("page") int page) {
        List<SearchPlacesResponseDto> responseDtoList =
                placeService
                        .searchPlaces(searchRequestDto.getContent(), page)
                        .stream()
                        .map(SearchPlacesResponseDto::new)
                        .toList();

        int next = placeService.checkNextSearchPlaces(searchRequestDto.getContent(), page);

        return new PageResult<>(responseDtoList, next);
    }

}
