package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TravelNoteItemDto {

    private Long travelNoteId;

    private String title;

    private String imageUrl;

    private List<String> theme;

    public TravelNoteItemDto(TravelNote travelNote) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = travelNote.getThumbnail();
        this.theme = travelNote.getTheme();
    }

}
