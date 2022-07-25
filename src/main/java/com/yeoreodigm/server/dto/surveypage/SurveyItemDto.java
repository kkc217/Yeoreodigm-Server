package com.yeoreodigm.server.dto.surveypage;

import com.yeoreodigm.server.domain.SurveyItem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SurveyItemDto {

    private String contentId;

    private String title;

    private String tag;

    private String imageUrl;

    public SurveyItemDto(SurveyItem surveyItem) {
        this.contentId = surveyItem.getContentId();
        this.title = surveyItem.getTitle();
        this.tag = surveyItem.getTag();
        this.imageUrl = surveyItem.getImageUrl();
    }

}