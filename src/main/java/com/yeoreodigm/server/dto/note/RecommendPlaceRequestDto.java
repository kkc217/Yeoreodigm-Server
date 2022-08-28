package com.yeoreodigm.server.dto.note;

import lombok.Data;

import java.util.List;

@Data
public class RecommendPlaceRequestDto {

    private Long travelNoteId;

    private int day;

    private List<Long> placeIdList;

}
