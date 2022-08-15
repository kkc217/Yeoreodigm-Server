package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MainPageTravelNote {

    private Long travelNoteId;

    private String title;

    private String imageUrl;

    private boolean hasLiked;

    private Long likeCount;

    public MainPageTravelNote(TravelNote travelNote, LikeItemDto likeItemDto) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = travelNote.getThumbnail();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
