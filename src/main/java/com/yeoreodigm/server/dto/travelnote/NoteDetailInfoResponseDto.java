package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class NoteDetailInfoResponseDto {

    private Long requestorId;

    private Long travelNoteId;

    private String title;

    private String period;

    private List<String> region;

    private List<String> theme;

    private String thumbnail;

    public NoteDetailInfoResponseDto(Member member, TravelNoteDetailInfo travelNoteInfo) {
        if (Objects.nonNull(member)) this.requestorId = member.getId();
        this.travelNoteId = travelNoteInfo.getTravelNoteId();
        this.title = travelNoteInfo.getTitle();
        this.period = travelNoteInfo.getPeriod();
        this.region = travelNoteInfo.getRegion();
        this.theme = travelNoteInfo.getTheme();
        this.thumbnail = travelNoteInfo.getThumbnail();
    }

}
