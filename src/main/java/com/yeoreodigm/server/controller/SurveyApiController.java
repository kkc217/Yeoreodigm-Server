package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.surveypage.SurveyItemDto;
import com.yeoreodigm.server.dto.surveypage.SurveyProgressResponseDto;
import com.yeoreodigm.server.dto.surveypage.SurveySubmitRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "survey", description = "설문 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyApiController {

    private final SurveyService surveyService;

    @GetMapping("/{progress}")
    public Result<List<SurveyItemDto>> callSurveyItems(
            @PathVariable("progress") int progress) {
        return new Result<>(surveyService.getSurveyItemsByProgress(progress));
    }

    @PostMapping("/submit/{progress}")
    public void submitSurveyResult(@PathVariable("progress") int progress,
                                   @RequestBody @Valid SurveySubmitRequestDto surveySubmitRequestDto,
                                   @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        surveyService.submitSurveyResult(member, surveySubmitRequestDto.getContentId(), progress);
    }

    @GetMapping("/progress")
    public SurveyProgressResponseDto callSurveyProgress(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new SurveyProgressResponseDto(surveyService.getProgress(member));
    }

}
