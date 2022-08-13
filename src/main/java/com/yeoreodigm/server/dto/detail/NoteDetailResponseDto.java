package com.yeoreodigm.server.dto.detail;

import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.CourseCoordinateDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NoteDetailResponseDto {

    private String title;

    private String period;

    private List<String> region;

    private List<String> theme;

    private String thumbnail;

    private boolean hasLiked;

    private Long likeCount;

    private List<CourseCoordinateDto> coordinates;

    private List<RecommendedTravelNotes> recommendedTravelNotes;

    private List<CommentItemDto> comments;

    public NoteDetailResponseDto(
            TravelNoteDetailInfo travelNoteInfo,
            LikeItemDto travelNoteLikeInfo,
            List<CourseCoordinateDto> coordinates,
            List<TravelNoteAndLikeDto> recommendedNoteList,
            List<CommentItemDto> commentList) {
        this.title = travelNoteInfo.getTitle();
        this.period = travelNoteInfo.getPeriod();
        this.region = travelNoteInfo.getRegion();
        this.theme = travelNoteInfo.getTheme();
        this.thumbnail = travelNoteInfo.getThumbnail();

        this.hasLiked = travelNoteLikeInfo.isHasLiked();
        this.likeCount = travelNoteLikeInfo.getLikeCount();

        this.coordinates = coordinates;

        this.recommendedTravelNotes = recommendedNoteList.stream().map(RecommendedTravelNotes::new).toList();

        this.comments = commentList;
    }

    @Data
    static class RecommendedTravelNotes {

        private Long travelNoteId;

        private String title;

        private String imageUrl;

        private List<String> theme;

        private boolean hasLiked;

        private Long likeCount;

        public RecommendedTravelNotes(TravelNoteAndLikeDto travelNoteAndLikeDto) {
            TravelNote travelNote = travelNoteAndLikeDto.getTravelNote();
            LikeItemDto likeItemDto = travelNoteAndLikeDto.getLikeItemDto();
            this.travelNoteId = travelNote.getId();
            this.title = travelNote.getTitle();
            this.imageUrl = travelNote.getThumbnail();
            this.theme = travelNote.getTheme();
            this.hasLiked = likeItemDto.isHasLiked();
            this.likeCount = likeItemDto.getLikeCount();
        }

    }

}
