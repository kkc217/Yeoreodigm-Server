package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceLikeDto {

    private Long placeId;

    private String title;

    private String address;

    private String imageUrl;

    private boolean hasLiked;

    private Long likeCount;

    public PlaceLikeDto(Places place, LikeItemDto likeItemDto) {
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.address = place.getAddress();
        this.imageUrl = place.getImageUrl();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
