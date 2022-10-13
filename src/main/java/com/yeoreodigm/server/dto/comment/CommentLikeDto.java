package com.yeoreodigm.server.dto.comment;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteComment;
import com.yeoreodigm.server.domain.PlaceComment;
import com.yeoreodigm.server.domain.board.BoardComment;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.Data;

import java.util.Objects;

@Data
public class CommentLikeDto {

    private Long commentId;

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String text;

    private boolean hasModified;

    private String dateTime;

    private boolean hasLiked;

    private Long likeCount;

    public CommentLikeDto(NoteComment noteComment, LikeItemDto likeItemDto) {
        Member member = noteComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(noteComment.getModified());

        this.commentId = noteComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = noteComment.getText();
        this.hasModified = !Objects.equals(noteComment.getCreated(), noteComment.getModified());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

    public CommentLikeDto(PlaceComment placeComment, LikeItemDto likeItemDto) {
        Member member = placeComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(placeComment.getModified());

        this.commentId = placeComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = placeComment.getText();
        this.hasModified = !Objects.equals(placeComment.getCreated(), placeComment.getModified());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

    public CommentLikeDto(BoardComment boardComment, LikeItemDto likeItemDto) {
        Member member = boardComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(boardComment.getModifiedTime());

        this.commentId = boardComment.getId();
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImage();
        this.text = boardComment.getText();
        this.hasModified = !Objects.equals(boardComment.getCreatedTime(), boardComment.getModifiedTime());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
