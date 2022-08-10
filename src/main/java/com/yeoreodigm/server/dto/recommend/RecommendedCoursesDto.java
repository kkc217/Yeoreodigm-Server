package com.yeoreodigm.server.dto.recommend;

import lombok.Data;

import java.util.List;

@Data
public class RecommendedCoursesDto {

    private List<List<Long>> courseList;

}