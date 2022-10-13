package com.yeoreodigm.server.dto.board;

import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardPlace;
import com.yeoreodigm.server.dto.constraint.AWSConst;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class BoardDto {

    private List<String> pictures;

    private String text;

    private Long travelNoteTag;

    private List<Long> placeTag = new ArrayList<>();

    public BoardDto(Board board) {
        this.pictures = board.getImageList()
                .stream()
                .map(address -> AWSConst.AWS_S3_BASE_URL + AWSConst.AWS_S3_BOARD_URI + "/" + address)
                .toList();
        this.text = board.getText();

        if (Objects.nonNull(board.getBoardTravelNote()))
            this.travelNoteTag = board.getBoardTravelNote().getTravelNote().getId();

        for (BoardPlace boardPlace : board.getBoardPlaceList()) {
            this.placeTag.add(boardPlace.getPlace().getId());
        }
    }

}
