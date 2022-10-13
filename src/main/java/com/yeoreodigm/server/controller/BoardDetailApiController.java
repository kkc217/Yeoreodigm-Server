package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.dto.board.BoardDetailDto;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.service.BoardCommentService;
import com.yeoreodigm.server.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/detail")
public class BoardDetailApiController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;

    @GetMapping("/{boardId}")
    public BoardDetailDto callBoardDetailInfo(
            @PathVariable("boardId") Long boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Board board = boardService.getBoardById(boardId);

        return new BoardDetailDto(
                board,
                Objects.nonNull(member) && Objects.equals(board.getMember().getId(), member.getId()),
                new DateTimeStr(board.getModifiedTime()));
    }

}
