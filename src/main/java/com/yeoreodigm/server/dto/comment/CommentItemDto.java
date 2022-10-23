package com.yeoreodigm.server.dto.comment;

import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import lombok.Data;

import java.util.Objects;

@Data
public class CommentItemDto {

    private Long commentId;

    private boolean mine;

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String text;

    private boolean hasModified;

    private String dateTime;

    public CommentItemDto(CourseComment comment, Member member) {
        Member owner = comment.getMember();
        DateTimeStr dateTimeStr = new DateTimeStr(comment.getModified());

        this.commentId = comment.getId();

        this.mine = Objects.nonNull(member) && Objects.equals(member.getId(), owner.getId());

        this.text = comment.getText();
        this.memberId = owner.getId();
        this.profileImageUrl = owner.getProfileImage();
        this.nickname = owner.getNickname();
        this.hasModified = !Objects.equals(comment.getCreated(), comment.getModified());
        this.dateTime = dateTimeStr.getDateTime();
    }

}
