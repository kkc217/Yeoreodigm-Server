package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainPagePlace {

    private Long placeId;

    private String title;

    private String imageUrl;

    public MainPagePlace(Places place) {
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.imageUrl = place.getImageUrl();
    }

}
