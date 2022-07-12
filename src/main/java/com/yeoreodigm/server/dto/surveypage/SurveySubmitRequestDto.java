package com.yeoreodigm.server.dto.surveypage;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SurveySubmitRequestDto {

    @NotEmpty
    private String contentId;

}