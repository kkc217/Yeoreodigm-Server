package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PublicTravelNoteDto {

    private Long travelNoteId;

    private String title;

    private String thumbnail;

    private List<String> region;

    private String period;

    private boolean hasLiked;

    private Long likeCount;

    private Long placeCount;

    private Long commentCount;

    public PublicTravelNoteDto(
            TravelNote travelNote, String period, LikeItemDto likeItemDto, Long placeCount, Long commentCount) {
        this.travelNoteId = travelNote.getId();
        this.title = travelNote.getTitle();
        this.thumbnail = travelNote.getThumbnail();
        if (travelNote.getRegion().size() > 3) {
            List<String> region = new ArrayList<>();
            region.add("제주도");
            this.region = region;
        } else {
            this.region = travelNote.getRegion();
        }
        this.period = period;
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
        this.placeCount = placeCount;
        this.commentCount = commentCount;
    }

}
