package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.noteprepare.PrepareResultRequestDto;
import com.yeoreodigm.server.dto.SearchPlacesResponseDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "note_prepare", description = "여행 메이킹 노트 준비 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/prepare")
public class NotePrepareApiController {

    private final PlaceService placeService;

    private final TravelNoteService travelNoteService;

    @GetMapping("/like/{page}")
    public PageResult<List<SearchPlacesResponseDto>> searchPlacesLike(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page) {
        if (member != null) {
            return new PageResult<>(
                        placeService
                            .searchPlaceLike(member, page)
                            .stream()
                            .map(SearchPlacesResponseDto::new)
                            .toList()
                        , placeService.checkNextPage(member, page));
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

    @PutMapping("/submit")
    public void submitPrepareResult(
            @RequestBody @Valid PrepareResultRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            TravelNote travelNote = TravelNote.builder()
                    .member(member)
                    .dayStart(requestDto.getDayStart())
                    .dayEnd(requestDto.getDayEnd())
                    .adult(requestDto.getAdult())
                    .child(requestDto.getChild())
                    .animal(requestDto.getAnimal())
                    .region(requestDto.getRegion())
                    .theme(requestDto.getTheme())
                    .course(requestDto.getPlaces())
                    .build();

            travelNoteService.submitNotePrepare(travelNote);
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

}
