package com.yeoreodigm.server.dto.travelnote;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.TravelNote;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MyTravelNoteDto {

    private Long travelNoteId;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayEnd;

    private String period;

    private int adult;

    private int child;

    private int animal;

    private String thumbnail;

    public MyTravelNoteDto(TravelNote travelNote, String period) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.dayStart = travelNote.getDayStart();
        this.dayEnd = travelNote.getDayEnd();
        this.period = period;
        this.adult = travelNote.getAdult();
        this.child = travelNote.getChild();
        this.animal = travelNote.getAnimal();
        this.thumbnail = travelNote.getThumbnail();
    }

}
