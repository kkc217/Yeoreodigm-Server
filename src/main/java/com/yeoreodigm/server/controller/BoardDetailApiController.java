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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/detail")
@Tag(name = "Board Detail", description = "피드 상세 페이지 API")
public class BoardDetailApiController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;

    private final MemberService memberService;

    @GetMapping("/{boardId}")
    @Operation(summary = "피드 정보 조회 (피드 상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
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
    @Operation(summary = "댓글 개수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
    public CountDto callBoardCommentCount(
            @PathVariable("boardId") Long boardId) {
        return new CountDto(
                boardCommentService.countCommentByBoard(
                        boardService.getBoardById(boardId)));
    }

    @GetMapping("/comment/{boardId}")
    @Operation(summary = "댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
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
    @Operation(summary = "댓글 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void addBoardComment(
            Authentication authentication,
            @RequestBody @Valid CommentRequestDto requestDto) {
        boardCommentService.addBoardComment(
                memberService.getMemberByAuth(authentication),
                boardService.getBoardById(requestDto.getId()),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteBoardComment(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        boardCommentService.deleteBoardComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/comment/like/{commentId}")
    @Operation(summary = "댓글 좋아요 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
    })
    public LikeItemDto callBoardCommentLike(
            Authentication authentication,
            @PathVariable("commentId") Long commentId) {
        return boardCommentService.getLikeInfo(
                boardCommentService.getBoardCommentById(commentId), memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/comment/like")
    @Operation(summary = "댓글 좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeBoardCommentLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        boardCommentService.changeBoardCommentLike(
                memberService.getMemberByAuth(authentication),
                boardCommentService.getBoardCommentById(requestDto.getId()), requestDto.isLike());
    }

}
