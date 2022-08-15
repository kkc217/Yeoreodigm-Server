package com.yeoreodigm.server.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteComment;
import com.yeoreodigm.server.domain.PlaceComment;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class CommentItemDto {

    private Long commentId;

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String text;

    private boolean hasModified;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime dateTime;

    private boolean hasLiked;

    private Long likeCount;

    public CommentItemDto(NoteComment noteComment) {
        Member member = noteComment.getMember();
        this.commentId = noteComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = noteComment.getText();
        this.hasModified = !Objects.equals(noteComment.getCreated(), noteComment.getModified());
        this.dateTime = noteComment.getModified();
        this.hasLiked = false;
        this.likeCount = 0L;
    }

    public CommentItemDto(NoteComment noteComment, LikeItemDto likeItemDto) {
        Member member = noteComment.getMember();
        this.commentId = noteComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = noteComment.getText();
        this.hasModified = !Objects.equals(noteComment.getCreated(), noteComment.getModified());
        this.dateTime = noteComment.getModified();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

    public CommentItemDto(PlaceComment placeComment, LikeItemDto likeItemDto) {
        Member member = placeComment.getMember();
        this.commentId = placeComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = placeComment.getText();
        this.hasModified = !Objects.equals(placeComment.getCreated(), placeComment.getModified());
        this.dateTime = placeComment.getModified();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
