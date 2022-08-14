package com.yeoreodigm.server.dto.search;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class PlaceResponseDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    public PlaceResponseDto(Places places) {
        this.placeId = places.getId();
        this.title = places.getTitle();
        this.address = places.getAddress();
        this.imageUrl = places.getImageUrl();
    }

}
