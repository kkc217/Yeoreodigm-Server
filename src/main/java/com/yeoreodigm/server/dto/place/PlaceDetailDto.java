package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_CHILDREN;
import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_PET;

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

    private boolean child;

    private boolean animal;

    public PlaceDetailDto(Places place) {
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.tag = place.getTag();
        this.introduction = place.getIntroduction();
        this.address = place.getAddress();
        this.imageUrl = place.getImageUrl();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.child = Objects.equals(place.getChildren(), NUMBER_OF_CHILDREN);
        this.animal = Objects.equals(place.getPet(), NUMBER_OF_PET);
    }

}
