package com.yeoreodigm.server.dto.comment;

import lombok.Data;

@Data
public class CourseCommentRequestDto {

    private Long travelNoteId;

    private int day;

    private String text;

}
