package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.detail.place.PlaceDetailResponseDto;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail/place")
public class PlaceDetailApiController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public PlaceDetailResponseDto callPlaceDetail(
            @PathVariable("placeId") Long placeId) {
        return new PlaceDetailResponseDto(placeService.getPlaceById(placeId));
    }
}
