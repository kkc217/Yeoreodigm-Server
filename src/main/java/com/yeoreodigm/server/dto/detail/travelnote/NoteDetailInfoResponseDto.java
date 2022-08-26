package com.yeoreodigm.server.dto.detail.travelnote;

import lombok.Data;

import java.util.List;

@Data
public class NoteDetailInfoResponseDto {

    private String title;

    private String period;

    private List<String> region;

    private List<String> theme;

    private String thumbnail;

    public NoteDetailInfoResponseDto(
            TravelNoteDetailInfo travelNoteInfo) {
        this.title = travelNoteInfo.getTitle();
        this.period = travelNoteInfo.getPeriod();
        this.region = travelNoteInfo.getRegion();
        this.theme = travelNoteInfo.getTheme();
        this.thumbnail = travelNoteInfo.getThumbnail();
    }

}
