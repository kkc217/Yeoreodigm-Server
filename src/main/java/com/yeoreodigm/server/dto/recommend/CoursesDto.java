package com.yeoreodigm.server.dto.recommend;

import lombok.Data;

import java.util.List;

@Data
public class CoursesDto {

    private List<List<Long>> courseList;

}