package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class SurveyItem {

    @Id @GeneratedValue
    private Long id;

    private Long placeId;

    private int progress;

    private String title;

    private String tag;

    private String imageUrl;

}
