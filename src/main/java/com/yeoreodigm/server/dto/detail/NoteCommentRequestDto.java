package com.yeoreodigm.server.dto.detail;

import lombok.Data;

@Data
public class NoteCommentRequestDto {

    private Long commentId;

    private Long travelNoteId;

    private String text;

}
