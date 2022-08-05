package com.yeoreodigm.server.dto.recommend;

import lombok.Data;

import java.util.List;

@Data
public class CoursesDto {

    private Long userid;

    private int day;

    private List<Long> include;

    private List<List<Long>> result_path;

}