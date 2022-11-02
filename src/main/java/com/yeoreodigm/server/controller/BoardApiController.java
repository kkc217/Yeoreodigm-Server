package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardTravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.board.BoardDto;
import com.yeoreodigm.server.dto.board.BoardFullDto;
import com.yeoreodigm.server.dto.board.BoardIdDto;
import com.yeoreodigm.server.dto.board.MyBoardDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BOARD_URI;
import static com.yeoreodigm.server.dto.constraint.BoardConst.MAX_NUM_OF_BOARD_PLACE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Tag(name = "Board", description = "피드 API")
public class BoardApiController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;

    private final TravelNoteService travelNoteService;

    private final MemberService memberService;

    private final AwsS3Service awsS3Service;

    @PostMapping("/new")
    @Operation(summary = "새 피드 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public BoardIdDto createBoard(
            Authentication authentication,
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures,
            @RequestPart(name = "text") String text,
            @RequestPart(name = "travelNoteTag", required = false) Long travelNoteTag,
            @RequestPart(name = "placeTag", required = false) List<Long> placeTag) {
        Member member = memberService.getMemberByAuth(authentication);

        boardService.validatePictures(pictures);
        List<String> pictureAddressList = awsS3Service.uploadFiles(
                AWS_S3_BOARD_URI, null, pictures);
        Board board = boardService.createBoard(member, pictureAddressList, text);

        BoardTravelNote boardTravelNote = boardService.createBoardTravelNote(board, travelNoteTag);

        boardService.createBoardPlaces(board, boardTravelNote, placeTag);

        if (Objects.nonNull(boardTravelNote))
            travelNoteService.changeNotePublicShare(boardTravelNote.getTravelNote(), true);

        return new BoardIdDto(board.getId());
    }

    @PutMapping("")
    @Operation(summary = "피드 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void editBoard(
            Authentication authentication,
            @RequestPart(name = "boardId") Long boardId,
            @RequestPart(name = "pictureUrl", required = false) List<String> pictureUrl,
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures,
            @RequestPart(name = "text") String text,
            @RequestPart(name = "travelNoteTag", required = false) Long travelNoteTag,
            @RequestPart(name = "placeTag", required = false) List<Long> placeTag) {
        Board board = boardService.getBoardById(boardId);

        Member member = memberService.getMemberByAuth(authentication);
        if (!Objects.equals(board.getMember().getId(), member.getId())) {
            throw new BadRequestException("여행 피드를 수정할 수 없습니다.");
        }

        boardService.validatePictures(pictureUrl, pictures);
        List<String> pictureAddressList = new ArrayList<>();
        pictureAddressList.addAll(boardService.getPictureNamesFromUrl(pictureUrl));
        pictureAddressList.addAll(awsS3Service.uploadFiles(AWS_S3_BOARD_URI, null, pictures));
        board.changeImageList(pictureAddressList);
        board.changeText(text);

        boardService.deleteBoardTravelNote(board.getBoardTravelNote());
        BoardTravelNote boardTravelNote = boardService.createBoardTravelNote(board, travelNoteTag);

        if (Objects.nonNull(placeTag) && MAX_NUM_OF_BOARD_PLACE < placeTag.size())
            throw new BadRequestException("여행지 태그는 최대 4개만 추가할 수 있습니다.");

        boardService.deleteBoardPlaceList(board.getBoardPlaceList());
        boardService.createBoardPlaces(board, boardTravelNote, placeTag);

        if (Objects.nonNull(boardTravelNote))
            travelNoteService.changeNotePublicShare(boardTravelNote.getTravelNote(), true);
    }

    @GetMapping("")
    @Operation(summary = "피드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<BoardFullDto>> callBoards(
            Authentication authentication,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option) {
        Member member = memberService.getMemberByAuth(authentication);

        return new PageResult<>(
                boardService.getBoardList(member, page, limit, option)
                        .stream()
                        .map(board -> new BoardFullDto(
                                board,
                                boardService.getLikeInfo(board, member),
                                boardCommentService.countCommentByBoard(board)))
                        .toList(),
                boardService.checkNextBoardList(member, page, limit, option));
    }

    @GetMapping("/modification/{boardId}")
    @Operation(summary = "피드 수정 페이지 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public BoardDto callBoardEditInfo(
            Authentication authentication,
            @PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardById(boardId);

        Member member = memberService.getMemberByAuth(authentication);
        if (!Objects.equals(board.getMember().getId(), member.getId())) {
            throw new BadRequestException("여행 피드를 수정할 수 없습니다.");
        }

        return new BoardDto(board);
    }

    @GetMapping("/my/{page}/{limit}")
    @Operation(summary = "내 피드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<MyBoardDto>> callMyBoards(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuth(authentication);

        List<Board> boardList = boardService.getMyBoardList(member, page, limit);

        return new PageResult<>(
                boardList
                        .stream()
                        .map(board -> new MyBoardDto(board, boardCommentService.countCommentByBoard(board)))
                        .toList(),
                boardService.checkNextMyBoardList(member, page, limit));
    }

    @GetMapping("/like/{boardId}")
    @Operation(summary = "피드 좋아요 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true)))
    })
    public LikeItemDto callBoardLike(
            Authentication authentication,
            @PathVariable("boardId") Long boardId) {
        return boardService.getLikeInfo(
                boardService.getBoardById(boardId), memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/like")
    @Operation(summary = "피드 좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeBoardLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        boardService.changeBoardLike(
                memberService.getMemberByAuth(authentication),
                boardService.getBoardById(requestDto.getId()), requestDto.isLike());
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "피드 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteBoard(
            Authentication authentication,
            @PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(memberService.getMemberByAuth(authentication), boardService.getBoardById(boardId));
    }

}
