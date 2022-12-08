package com.yeoreodigm.server.dto.accommodation;

import com.yeoreodigm.server.domain.Accommodation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccommodationDto implements Serializable {

    private Long accommodationId;

    private String title;

    private String address;

    private String introduction;

    private String dialNum;

    private String type;

    private String imageUrl;

    private int score;

    private List<String> tag;

    private double latitude;

    private double longitude;

    public AccommodationDto(Accommodation accommodation) {
        this.accommodationId = accommodation.getId();
        this.title = accommodation.getTitle();
        this.address = accommodation.getAddress();
        this.introduction = accommodation.getIntroduction();
        this.dialNum = accommodation.getDialNum();
        this.type = accommodation.getType().getKName();
        this.imageUrl = accommodation.getImageUrl();
        this.score = accommodation.getScore();
        this.tag = accommodation.getTag();
        this.latitude = accommodation.getLatitude();
        this.longitude = accommodation.getLongitude();
    }

}
