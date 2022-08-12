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

    public MainPageTravelNote(TravelNote travelNote, String imageUrl) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = imageUrl;
    }

}
