package com.yeoreodigm.server.dto.detail.travelnote;

import lombok.Data;

@Data
public class LikeRequestDto {

    private Long travelNoteId;

    private Long commentId;

    private boolean like;

}
