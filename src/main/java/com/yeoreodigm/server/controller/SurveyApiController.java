package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.LoginMemberDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.surveypage.SurveySubmitRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.NoSuchElementException;

@Tag(name = "survey", description = "설문 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyApiController {

    private final SurveyService surveyService;

    @GetMapping("/{progress}")
    public Result surveyInfo(@PathVariable("progress") int progress) {
        return new Result(surveyService.getSurveyInfo(progress));
    }

    @PostMapping("/submit/{progress}")
    public void surveySubmit(@PathVariable("progress") int progress, HttpServletRequest httpServletRequest,
                             @RequestBody @Valid SurveySubmitRequestDto surveySubmitRequestDto) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            LoginMemberDto loginMemberDto = (LoginMemberDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
            surveyService.putSurveyResult(loginMemberDto.getEmail(), surveySubmitRequestDto.getContentId(), progress);
        } else {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
