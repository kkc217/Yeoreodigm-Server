package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.survey.SurveyItemDto;
import com.yeoreodigm.server.dto.survey.SurveyProgressResponseDto;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyApiController {

    private final SurveyService surveyService;

    private final MemberService memberService;

    @GetMapping("/{progress}")
    public Result<List<SurveyItemDto>> callSurveyItems(
            @PathVariable("progress") int progress) {
        return new Result<>(surveyService.getSurveyItemsByProgress(progress));
    }

    @PostMapping("/result")
    public void submitSurveyResult(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        surveyService.submitSurveyResult(
                memberService.getMemberByAuth(authentication),
                Long.parseLong(request.get("contentId")),
                Integer.parseInt(request.get("progress")));
    }

    @GetMapping("/progress")
    public SurveyProgressResponseDto callSurveyProgress(Authentication authentication) {
        return new SurveyProgressResponseDto(surveyService.getProgress(memberService.getMemberByAuth(authentication)));
    }

}
