package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteResponseDto;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteRequestDto;
import com.yeoreodigm.server.dto.place.PlaceResponseDto;
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

    private final TravelNoteService travelNoteService;

    private final CourseService courseService;

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
