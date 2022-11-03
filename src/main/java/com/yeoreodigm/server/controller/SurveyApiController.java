package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.survey.SurveyItemDto;
import com.yeoreodigm.server.dto.survey.SurveyProgressResponseDto;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
@Tag(name = "Survey", description = "설문 API")
public class SurveyApiController {

    private final SurveyService surveyService;

    private final MemberService memberService;

    @GetMapping("/{progress}")
    @Operation(summary = "설문 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<SurveyItemDto>> callSurveyItems(
            @PathVariable("progress") int progress) {
        return new Result<>(surveyService.getSurveyItemsByProgress(progress));
    }

    @PostMapping("/result")
    @Operation(summary = "설문 결과 제출")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void submitSurveyResult(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        surveyService.submitSurveyResult(
                memberService.getMemberByAuth(authentication),
                Long.parseLong(request.get("contentId")),
                Integer.parseInt(request.get("progress")));
    }

    @GetMapping("/progress")
    @Operation(summary = "멤버 설문 번호 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public SurveyProgressResponseDto callSurveyProgress(Authentication authentication) {
        return new SurveyProgressResponseDto(surveyService.getProgress(memberService.getMemberByAuth(authentication)));
    }

}
