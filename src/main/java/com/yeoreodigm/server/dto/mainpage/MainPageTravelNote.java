package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainPageTravelNote {

    private Long travelNoteId;

    private String title;

    private String imageUrl;

    public MainPageTravelNote(TravelNote travelNote) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = travelNote.getThumbnail();
    }

}
