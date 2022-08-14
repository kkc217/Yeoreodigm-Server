package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteResponseDto;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteRequestDto;
import com.yeoreodigm.server.dto.search.PlaceResponseDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.service.CourseService;
import com.yeoreodigm.server.service.PlaceLikeService;
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

    private final PlaceLikeService placeLikeService;

    private final TravelNoteService travelNoteService;

    private final CourseService courseService;

    @GetMapping("/like/{page}")
    public PageResult<List<PlaceResponseDto>> callPlacesLike(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page) {
        List<PlaceLike> placeLikeList
                = placeLikeService.getPlaceLikesByMemberPaging(member, page, QueryConst.PLACE_LIKE_PAGING_LIMIT);
        List<Places> placeList = placeService.getPlacesByPlaceLikes(placeLikeList);

        int next = placeLikeService.checkNextPlaceLikePage(member, page, QueryConst.PLACE_LIKE_PAGING_LIMIT);

        return new PageResult<>(placeList.stream().map(PlaceResponseDto::new).toList(), next);
    }

    @PutMapping("/submit")
    public TravelNoteResponseDto submitPrepareResult(
            @RequestBody @Valid NewTravelNoteRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.createTravelNote(member, requestDto);
        Long travelNoteId = travelNoteService.submitNotePrepare(travelNote);

        courseService.optimizeCourse(travelNote);

        return new TravelNoteResponseDto(travelNoteId);
    }

}
