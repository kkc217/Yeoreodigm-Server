package com.yeoreodigm.server.dto.noteprepare;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PrepareResultRequestDto {

    @NotEmpty
    private String dayStart;

    @NotEmpty
    private String dayEnd;

    @NotEmpty
    private int adult;

    @NotEmpty
    private int child;

    @NotEmpty
    private int animal;

    @NotEmpty
    private String region;

    private String theme;

    private List<Long> places;

}
