package com.yeoreodigm.server.dto.detail;

import lombok.Data;

@Data
public class AddNoteCommentRequestDto {

    private Long travelNoteId;

    private String text;

}
