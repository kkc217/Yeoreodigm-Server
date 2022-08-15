package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainPagePlace {

    private Long placeId;

    private String title;

    private String imageUrl;

    private boolean hasLiked;

    private Long likeCount;

    public MainPagePlace(Places place, LikeItemDto likeItemDto) {
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.imageUrl = place.getImageUrl();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
