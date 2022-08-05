package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class AddRecommendRequestDto {

    private Long travelNoteId;

    private int day;

    private Long placeId;

}
