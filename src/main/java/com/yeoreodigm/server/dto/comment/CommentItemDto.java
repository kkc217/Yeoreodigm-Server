package com.yeoreodigm.server.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
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

    public CommentItemDto(CourseComment comment) {
        Member member = comment.getMember();

        this.commentId = comment.getId();
        this.text = comment.getText();
        this.memberId = member.getId();
        this.profileImageUrl = member.getProfileImage();
        this.nickname = member.getNickname();
        this.hasModified = !Objects.equals(comment.getCreated(), comment.getModified());
        this.dateTime = comment.getModified();
    }

}
