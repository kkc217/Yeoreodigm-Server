package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardComment;
import com.yeoreodigm.server.dto.CountDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.board.BoardDetailDto;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.comment.CommentRequestDto;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.service.BoardCommentService;
import com.yeoreodigm.server.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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

    @GetMapping("/comment/count/{boardId}")
    public CountDto callBoardCommentCount(
            @PathVariable("boardId") Long boardId) {
        return new CountDto(
                boardCommentService.countCommentByBoard(
                        boardService.getBoardById(boardId)));
    }

    @GetMapping("/comment/{boardId}")
    public Result<List<CommentLikeDto>> callBoardComment(
            @PathVariable("boardId") Long boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(
                boardCommentService.getBoardCommentsByBoard(boardService.getBoardById(boardId))
                        .stream()
                        .map(boardComment -> new CommentLikeDto(
                                boardComment, boardCommentService.getLikeInfo(boardComment, member)))
                        .toList());
    }

    @PostMapping("/comment")
    public void addBoardComment(
            @RequestBody @Valid CommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        boardCommentService.addBoardComment(
                member,
                boardService.getBoardById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteBoardComment(
            @PathVariable("commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        boardCommentService.deleteBoardComment(member, commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    public LikeItemDto callBoardCommentLike(
            @PathVariable("commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return boardCommentService.getLikeInfo(boardCommentService.getBoardCommentById(commentId), member);
    }

    @PatchMapping("/comment/like")
    public void changeBoardCommentLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        boardCommentService.changeBoardCommentLike(
                member, boardCommentService.getBoardCommentById(requestDto.getId()), requestDto.isLike());
    }

}
