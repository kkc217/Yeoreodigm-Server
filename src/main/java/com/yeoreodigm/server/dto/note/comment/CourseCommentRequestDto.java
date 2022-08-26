package com.yeoreodigm.server.dto.note.comment;

import lombok.Data;

@Data
public class CourseCommentRequestDto {

    private Long travelNoteId;

    private int day;

    private String text;

}
