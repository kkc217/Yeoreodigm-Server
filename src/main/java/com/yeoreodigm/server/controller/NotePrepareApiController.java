package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.noteprepare.SubmitPrepareResponseDto;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteDto;
import com.yeoreodigm.server.dto.search.SearchPlacesResponseDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.CourseService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "note_prepare", description = "여행 메이킹 노트 준비 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/prepare")
public class NotePrepareApiController {

    private final PlaceService placeService;

    private final TravelNoteService travelNoteService;

    private final CourseService courseService;

    @GetMapping("/like/{page}")
    public PageResult<List<SearchPlacesResponseDto>> searchPlacesLike(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page) {
        if (page <= 0) {
            throw new BadRequestException("잘못된 페이지 요청입니다.");
        }
        if (member != null) {
            return new PageResult<>(
                        placeService
                            .searchPlaceLike(member, page, QueryConst.PAGING_LIMIT_PUBLIC)
                            .stream()
                            .map(SearchPlacesResponseDto::new)
                            .toList()
                        , placeService.checkNextLikePage(member, page, QueryConst.PAGING_LIMIT_PUBLIC));
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

    @PutMapping("/submit")
    public SubmitPrepareResponseDto submitPrepareResult(
            @RequestBody @Valid NewTravelNoteDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.createTravelNote(member, requestDto);
        Long travelNoteId = travelNoteService.submitNotePrepare(travelNote);

        courseService.optimizeCourse(travelNoteId);

        return new SubmitPrepareResponseDto(travelNoteId);
    }

}
