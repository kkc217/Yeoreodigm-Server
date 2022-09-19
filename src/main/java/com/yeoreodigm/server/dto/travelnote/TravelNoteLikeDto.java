package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TravelNoteLikeDto {

    private Long travelNoteId;

    private String title;

    private String imageUrl;

    private List<String> theme;

    private boolean hasLiked;

    private Long likeCount;

    public TravelNoteLikeDto(TravelNote travelNote, LikeItemDto likeItemDto) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.imageUrl = travelNote.getThumbnail();
        this.theme = travelNote.getTheme();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
