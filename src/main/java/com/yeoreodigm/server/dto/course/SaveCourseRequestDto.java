package com.yeoreodigm.server.dto.course;

import lombok.Data;

import java.util.List;

@Data
public class SaveCourseRequestDto {

    private Long travelNoteId;

    private List<List<Long>> courseList;

}
