package com.yeoreodigm.server.dto.board;

import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import lombok.Data;

import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BASE_URL;
import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BOARD_URI;

@Data
public class MyBoardDto {

    private Long boardId;

    private String text;

    private String thumbnail;

    private boolean singleImage;

    private String dateTime;

    private Long commentCount;

    public MyBoardDto(Board board, Long commentCount) {
        this.boardId = board.getId();
        this.text = board.getText();
        this.thumbnail = AWS_S3_BASE_URL + AWS_S3_BOARD_URI + "/" + board.getImageList().get(0);
        this.singleImage = Objects.equals(1, board.getImageList().size());

        DateTimeStr dateTimeStr = new DateTimeStr(board.getModifiedTime());
        this.dateTime = dateTimeStr.getDateTime();

        this.commentCount = commentCount;
    }

}
