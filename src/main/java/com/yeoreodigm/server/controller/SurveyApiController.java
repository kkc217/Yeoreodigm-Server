package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.survey.SurveyItemDto;
import com.yeoreodigm.server.dto.survey.SurveyProgressResponseDto;
import com.yeoreodigm.server.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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

    @PostMapping("/{progress}")
    public void submitSurveyResult(
            @PathVariable("progress") int progress,
            @RequestBody HashMap<String, Long> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        surveyService.submitSurveyResult(member, request.get("contentId"), progress);
    }

    @GetMapping("/progress")
    public SurveyProgressResponseDto callSurveyProgress(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new SurveyProgressResponseDto(surveyService.getProgress(member));
    }

}
