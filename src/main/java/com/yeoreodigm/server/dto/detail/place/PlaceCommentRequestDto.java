package com.yeoreodigm.server.dto.detail.place;

import lombok.Data;

@Data
public class PlaceCommentRequestDto {

    private Long placeId;

    private String text;

}
