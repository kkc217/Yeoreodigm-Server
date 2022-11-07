package com.yeoreodigm.server.dto.travelnote;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class NoteDetailInfoResponseDto {

    private Long travelNoteId;

    private String title;

    private String period;

    private List<String> region;

    private List<String> theme;

    private String thumbnail;

    public NoteDetailInfoResponseDto(TravelNoteDetailInfo travelNoteInfo) {
        this.travelNoteId = travelNoteInfo.getTravelNoteId();
        this.title = travelNoteInfo.getTitle();
        this.period = travelNoteInfo.getPeriod();

        List<String> region = travelNoteInfo.getRegion();
        List<String> theme = travelNoteInfo.getTheme();
        this.region = region;
        this.theme = Objects.equals(0, theme.size()) ? region : theme;

        this.thumbnail = travelNoteInfo.getThumbnail();
    }

}
