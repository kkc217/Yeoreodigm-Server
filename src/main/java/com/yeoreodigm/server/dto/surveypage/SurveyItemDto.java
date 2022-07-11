package com.yeoreodigm.server.dto.surveypage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SurveyItemDto {

    private String contentId;

    private String title;

    private String tag;

    private String imageUrl;

}