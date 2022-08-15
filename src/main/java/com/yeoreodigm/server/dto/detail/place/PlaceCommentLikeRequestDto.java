package com.yeoreodigm.server.dto.detail.place;

import lombok.Data;

@Data
public class PlaceCommentLikeRequestDto {

    private Long commentId;

    private boolean like;

}
