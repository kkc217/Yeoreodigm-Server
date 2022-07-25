package com.yeoreodigm.server.dto.noteprepare;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

@Data
public class SearchPlacesLikeResponseDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    public SearchPlacesLikeResponseDto(Places places) {
        this.placeId = places.getId();
        this.title = places.getTitle();
        this.address = places.getAddress();
        this.imageUrl = places.getImageUrl();
    }

}
