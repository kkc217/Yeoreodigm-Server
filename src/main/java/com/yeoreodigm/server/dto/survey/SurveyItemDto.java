package com.yeoreodigm.server.dto.survey;

import com.yeoreodigm.server.domain.SurveyItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SurveyItemDto implements Serializable {

    private Long contentId;

    private String title;

    private String tag;

    private String imageUrl;

    public SurveyItemDto(SurveyItem surveyItem) {
        this.contentId = surveyItem.getPlaceId();
        this.title = surveyItem.getTitle();
        this.tag = surveyItem.getTag();
        this.imageUrl = surveyItem.getImageUrl();
    }

}