package com.yeoreodigm.server.dto.detail;

import lombok.Data;

@Data
public class LikeRequestDto {

    private Long travelNoteId;

    private Long commentId;

    private boolean like;

}
