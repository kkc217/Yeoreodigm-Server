package com.yeoreodigm.server.dto.travelnote;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewTravelNoteRequestDto {

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
