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

    private boolean mine;

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String text;

    private boolean hasModified;

    private String dateTime;

    private boolean hasLiked;

    private Long likeCount;

    public CommentLikeDto(NoteComment noteComment, Member member, LikeItemDto likeItemDto) {
        Member owner = noteComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(noteComment.getModified());

        this.commentId = noteComment.getId();

        this.mine = Objects.nonNull(member) && Objects.equals(member.getId(), owner.getId());

        this.memberId = owner.getId();
        this.nickname = owner.getNickname();
        this.profileImageUrl = owner.getProfileImage();
        this.text = noteComment.getText();
        this.hasModified = !Objects.equals(noteComment.getCreated(), noteComment.getModified());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

    public CommentLikeDto(PlaceComment placeComment, Member member, LikeItemDto likeItemDto) {
        Member owner = placeComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(placeComment.getModified());

        this.commentId = placeComment.getId();

        this.mine = Objects.nonNull(member) && Objects.equals(member.getId(), owner.getId());

        this.memberId = owner.getId();
        this.nickname = owner.getNickname();
        this.profileImageUrl = owner.getProfileImage();
        this.text = placeComment.getText();
        this.hasModified = !Objects.equals(placeComment.getCreated(), placeComment.getModified());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

    public CommentLikeDto(BoardComment boardComment, Member member, LikeItemDto likeItemDto) {
        Member owner = boardComment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(boardComment.getModifiedTime());

        this.commentId = boardComment.getId();

        this.mine = Objects.nonNull(member) && Objects.equals(member.getId(), owner.getId());

        this.memberId = owner.getId();
        this.nickname = owner.getNickname();
        this.profileImageUrl = owner.getProfileImage();
        this.text = boardComment.getText();
        this.hasModified = !Objects.equals(boardComment.getCreatedTime(), boardComment.getModifiedTime());
        this.dateTime = dateTimeStr.getDateTime();
        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
    }

}
