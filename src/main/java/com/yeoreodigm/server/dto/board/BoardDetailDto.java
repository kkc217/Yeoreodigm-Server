package com.yeoreodigm.server.dto.board;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import com.yeoreodigm.server.dto.constraint.AWSConst;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class BoardDetailDto {

    private Long boardId;

    private boolean mine;

    private Long memberId;

    private String profileImage;

    private String nickname;

    private String dateTime;

    private String text;

    private List<String> imageList;

    private int imageCount;

    private Long travelNoteTag;

    private List<Long> placeTag;

    public BoardDetailDto(Board board, Member member, DateTimeStr dateTimeStr) {
        Member owner = board.getMember();

        this.boardId = board.getId();

        this.mine = Objects.nonNull(member) && Objects.equals(member.getId(), owner.getId());

        this.memberId = owner.getId();
        this.profileImage = owner.getProfileImage();
        this.nickname = owner.getNickname();

        this.dateTime = dateTimeStr.getDateTime();

        this.text = board.getText();
        this.imageList = board.getImageList()
                .stream()
                .map(address -> AWSConst.AWS_S3_BASE_URL + AWSConst.AWS_S3_BOARD_URI + "/" + address)
                .toList();
        this.imageCount = imageList.size();

        if (Objects.nonNull(board.getBoardTravelNote()))
            this.travelNoteTag = board.getBoardTravelNote().getTravelNote().getId();

        if (Objects.nonNull(board.getBoardPlaceList()))
            this.placeTag = board.getBoardPlaceList()
                    .stream()
                    .map(boardPlace -> boardPlace.getPlace().getId())
                    .toList();
    }

}
