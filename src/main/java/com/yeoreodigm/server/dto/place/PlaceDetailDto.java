package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.PlacesEn;
import com.yeoreodigm.server.domain.PlacesZh;
import lombok.Data;

import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_CHILDREN;
import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_PET;

@Data
public class PlaceDetailDto {

    private Long requestorId;

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

    public PlaceDetailDto(Member member, Places place) {
        if (Objects.nonNull(member)) this.requestorId = member.getId();
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

    public PlaceDetailDto(Member member, Places place, PlacesEn placeEn) {
        if (Objects.nonNull(member)) this.requestorId = member.getId();
        this.placeId = place.getId();

        this.title = placeEn.getTitle();
        this.tag = placeEn.getTag();
        this.introduction = placeEn.getIntroduction();
        this.address = placeEn.getAddress();

        this.imageUrl = place.getImageUrl();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.child = Objects.equals(place.getChildren(), NUMBER_OF_CHILDREN);
        this.animal = Objects.equals(place.getPet(), NUMBER_OF_PET);
    }

    public PlaceDetailDto(Member member, Places place, PlacesZh placeZh) {
        if (Objects.nonNull(member)) this.requestorId = member.getId();
        this.placeId = place.getId();

        this.title = placeZh.getTitle();
        this.tag = placeZh.getTag();
        this.introduction = placeZh.getIntroduction();
        this.address = placeZh.getAddress();

        this.imageUrl = place.getImageUrl();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.child = Objects.equals(place.getChildren(), NUMBER_OF_CHILDREN);
        this.animal = Objects.equals(place.getPet(), NUMBER_OF_PET);
    }

}
