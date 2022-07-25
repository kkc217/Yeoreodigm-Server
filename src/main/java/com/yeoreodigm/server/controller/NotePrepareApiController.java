package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.noteprepare.SearchPlacesLikeResponseDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.service.PlaceLikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "note_prepare", description = "여행 메이킹 노트 준비 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note/prepare")
public class NotePrepareApiController {

    private final PlaceLikeService placeLikeService;

    @GetMapping("/like/{page}")
    public Result<SearchPlacesLikeResponseDto> searchPlacesLike(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
            @PathVariable("page") int page
    ) {
        return new Result<>(
                        (SearchPlacesLikeResponseDto) placeLikeService
                                .searchPlaceLike(member, page)
                                .stream()
                                .map(SearchPlacesLikeResponseDto::new)
                                .toList()
                    );
    }

}
