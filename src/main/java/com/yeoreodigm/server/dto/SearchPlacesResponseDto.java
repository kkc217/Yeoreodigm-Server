package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class SearchPlacesResponseDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    public SearchPlacesResponseDto(Places places) {
        this.placeId = places.getId();
        this.title = places.getTitle();
        this.address = places.getAddress();
        this.imageUrl = places.getImageUrl();
    }

}
