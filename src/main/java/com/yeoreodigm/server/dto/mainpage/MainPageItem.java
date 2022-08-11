package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainPageItem {

    private Long id;

    private String title;

    private String imageUrl;

    public MainPageItem(TravelNote travelNote, String imageUrl) {
        this.id = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = imageUrl;
    }

    public MainPageItem(Places place) {
        this.id = place.getId();
        this.title = place.getTitle();
        this.imageUrl = place.getImageUrl();
    }

}
