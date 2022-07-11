package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.surveypage.SurveyItemDto;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "survey", description = "설문 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyApiController {

    private final SurveyService surveyService;

    @GetMapping("/{progress}")
    public Result surveyInfo(@PathVariable("progress") int group) {
        System.out.println(group);
        List<SurveyItemDto> surveyInfoList = surveyService.getSurveyInfo(group);

        return new Result(surveyInfoList);
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
