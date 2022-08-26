package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class PlaceResponseDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    private double latitude;

    private double longitude;

    public PlaceResponseDto(Places places) {
        this.placeId = places.getId();
        this.title = places.getTitle();
        this.address = places.getAddress();
        this.imageUrl = places.getImageUrl();
        this.latitude = places.getLatitude();
        this.longitude = places.getLongitude();
    }

}
