package com.yeoreodigm.server.dto.place.detail;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class PlaceDetailResponseDto {

    private String title;

    private String tag;

    private String introduction;

    private String address;

    private String imageUrl;

    private double latitude;

    private double longitude;

    public PlaceDetailResponseDto(Places place) {
        this.title = place.getTitle();
        this.tag = place.getTag();
        this.introduction = place.getIntroduction();
        this.address = place.getAddress();
        this.imageUrl = place.getImageUrl();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
    }

}
