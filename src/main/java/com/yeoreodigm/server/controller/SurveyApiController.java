package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.LoginResponseDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.surveypage.SurveyItemDto;
import com.yeoreodigm.server.dto.surveypage.SurveyProgressResponseDto;
import com.yeoreodigm.server.dto.surveypage.SurveySubmitRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "survey", description = "설문 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyApiController {

    private final SurveyService surveyService;

    private final MemberService memberService;

    @GetMapping("/{progress}")
    public Result<List<SurveyItemDto>> surveyInfo(@PathVariable("progress") int progress) {
        return new Result<>(surveyService.getSurveyInfo(progress));
    }

    @PostMapping("/submit/{progress}")
    public void surveySubmit(@PathVariable("progress") int progress,
                             @RequestBody @Valid SurveySubmitRequestDto surveySubmitRequestDto,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            surveyService.submitSurveyResult(member, surveySubmitRequestDto.getContentId(), progress);
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

    @GetMapping("/progress")
    public SurveyProgressResponseDto surveyProgress(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            return new SurveyProgressResponseDto(surveyService.getProgress(member));
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

}
