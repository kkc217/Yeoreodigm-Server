package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.dto.CountDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.board.BoardDetailDto;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.comment.CommentRequestDto;
import com.yeoreodigm.server.dto.comment.DateTimeStr;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.service.BoardCommentService;
import com.yeoreodigm.server.service.BoardService;
import com.yeoreodigm.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/detail")
public class BoardDetailApiController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;

    private final MemberService memberService;

    @GetMapping("/{boardId}")
    public BoardDetailDto callBoardDetailInfo(
            Authentication authentication,
            @PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardById(boardId);

        return new BoardDetailDto(
                board,
                memberService.getMemberByAuth(authentication),
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
            Authentication authentication,
            @PathVariable("boardId") Long boardId) {
        Member member = memberService.getMemberByAuth(authentication);

        return new Result<>(
                boardCommentService.getBoardCommentsByBoard(boardService.getBoardById(boardId))
                        .stream()
                        .map(boardComment -> new CommentLikeDto(
                                boardComment, member, boardCommentService.getLikeInfo(boardComment, member)))
                        .toList());
    }

    @PostMapping("/comment")
    public void addBoardComment(
            Authentication authentication,
            @RequestBody @Valid CommentRequestDto requestDto) {
        boardCommentService.addBoardComment(
                memberService.getMemberByAuth(authentication),
                boardService.getBoardById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteBoardComment(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        boardCommentService.deleteBoardComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    public LikeItemDto callBoardCommentLike(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        return boardCommentService.getLikeInfo(
                boardCommentService.getBoardCommentById(commentId), memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/comment/like")
    public void changeBoardCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        boardCommentService.changeBoardCommentLike(
                memberService.getMemberByAuth(authentication),
                boardCommentService.getBoardCommentById(requestDto.getId()), requestDto.isLike());
    }

}
