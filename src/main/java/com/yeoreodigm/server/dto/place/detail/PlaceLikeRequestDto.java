package com.yeoreodigm.server.dto.place.detail;

import lombok.Data;

@Data
public class PlaceLikeRequestDto {

    private Long placeId;

    private boolean like;

}
