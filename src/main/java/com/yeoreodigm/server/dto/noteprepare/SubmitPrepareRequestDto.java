package com.yeoreodigm.server.dto.noteprepare;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Data
public class SubmitPrepareRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dayStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dayEnd;

    private int adult;

    private int child;

    private int animal;

    private List<String> region;

    private List<String> theme;

    private List<Long> places;

}
