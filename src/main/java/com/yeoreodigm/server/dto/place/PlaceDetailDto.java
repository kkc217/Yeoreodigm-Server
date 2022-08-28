package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class PlaceDetailDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    private String tag;

    private String introduction;

    private double latitude;

    private double longitude;

    public PlaceDetailDto(Places place) {
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.tag = place.getTag();
        this.introduction = place.getIntroduction();
        this.address = place.getAddress();
        this.imageUrl = place.getImageUrl();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
    }

}
