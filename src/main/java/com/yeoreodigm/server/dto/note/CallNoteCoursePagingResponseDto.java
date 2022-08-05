package com.yeoreodigm.server.dto.note;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CallNoteCoursePagingResponseDto {

    private int day;

    private List<PlaceInfo> places = new ArrayList<>();

    private List<CommentInfo> comments = new ArrayList<>();

    public CallNoteCoursePagingResponseDto(int day, List<Places> placeList, List<CourseComment> commentList) {
        this.day = day;

        for (int idx = 0; idx < placeList.size(); idx++) {
            Places place = placeList.get(idx);
            if (idx != placeList.size() - 1) {
                this.places.add(new PlaceInfo(place.getId(), place.getTitle(), place.getImageUrl(), place.getAddress(), false));
            } else {
                this.places.add(new PlaceInfo(place.getId(), place.getTitle(), place.getImageUrl(), place.getAddress(), true));
            }
        }

        for (CourseComment comment : commentList) {
            Member member = comment.getMember();

            boolean isModified = !comment.getCreated().isEqual(comment.getModified());
            LocalDateTime dateTime = isModified ? comment.getModified() : comment.getCreated();


            this.comments.add(new CommentInfo(
                    comment.getId(),
                    comment.getText(),
                    member.getId(),
                    member.getProfileImage(),
                    member.getNickname(),
                    isModified,
                    dateTime
                    ));
        }
    }

    @Getter
    @AllArgsConstructor
    static class PlaceInfo {

        private Long placeId;

        private String title;

        private String imageUrl;

        private String address;

        private boolean hasNext;

    }

    @Getter
    @AllArgsConstructor
    static class CommentInfo {

        private Long commentId;

        private String text;

        private Long memberId;

        private String imageUrl;

        private String nickname;

        private boolean hasModified;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime dateTime;

    }

}
