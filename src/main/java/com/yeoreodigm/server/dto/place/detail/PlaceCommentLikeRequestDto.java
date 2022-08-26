package com.yeoreodigm.server.dto.place.detail;

import lombok.Data;

@Data
public class PlaceCommentLikeRequestDto {

    private Long commentId;

    private boolean like;

}
