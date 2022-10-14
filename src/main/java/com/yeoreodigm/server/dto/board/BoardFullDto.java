package com.yeoreodigm.server.dto.board;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.Data;

import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BASE_URL;
import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BOARD_URI;

@Data
public class BoardFullDto {

    private Long memberId;

    private String profileImage;

    private String nickname;

    private Long boardId;

    private String text;

    private String thumbnail;

    private boolean singleImage;

    private String dateTime;

    private boolean hasLiked;

    private Long likeCount;

    private Long commentCount;

    public BoardFullDto(Board board, LikeItemDto likeItemDto, Long commentCount) {
        Member member = board.getMember();
        this.memberId = member.getId();
        this.profileImage = member.getProfileImage();
        this.nickname = member.getNickname();

        this.boardId = board.getId();
        this.text = board.getText();
        this.thumbnail = AWS_S3_BASE_URL + AWS_S3_BOARD_URI + "/" + board.getImageList().get(0);
        this.singleImage = Objects.equals(1, board.getImageList().size());

        DateTimeStr dateTimeStr = new DateTimeStr(board.getModifiedTime());
        this.dateTime = dateTimeStr.getDateTime();

        this.hasLiked = likeItemDto.isHasLiked();
        this.likeCount = likeItemDto.getLikeCount();
        this.commentCount = commentCount;
    }

}
