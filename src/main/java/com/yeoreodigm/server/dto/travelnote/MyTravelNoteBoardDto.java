package com.yeoreodigm.server.dto.travelnote;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyTravelNoteBoardDto {

    private Long travelNoteId;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayEnd;

    private String thumbnail;

    private boolean publicShare;

    private Long placeCount;

    public MyTravelNoteBoardDto(TravelNote travelNote, Long placeCount) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.dayStart = travelNote.getDayStart();
        this.dayEnd = travelNote.getDayEnd();
        this.thumbnail = travelNote.getThumbnail();
        this.publicShare = travelNote.isPublicShare();
        this.placeCount = placeCount;
    }

}
