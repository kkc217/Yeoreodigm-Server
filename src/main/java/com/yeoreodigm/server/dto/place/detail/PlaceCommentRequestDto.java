package com.yeoreodigm.server.dto.place.detail;

import lombok.Data;

@Data
public class PlaceCommentRequestDto {

    private Long commentId;

    private Long placeId;

    private String text;

}
