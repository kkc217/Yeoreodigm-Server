package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardTravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.board.BoardDto;
import com.yeoreodigm.server.dto.board.BoardFullDto;
import com.yeoreodigm.server.dto.board.BoardIdDto;
import com.yeoreodigm.server.dto.board.MyBoardDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BOARD_URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

    private final BoardCommentService boardCommentService;

    private final AwsS3Service awsS3Service;

    @PostMapping("/new")
    public BoardIdDto createBoard(
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures,
            @RequestPart(name = "text") String text,
            @RequestPart(name = "travelNoteTag", required = false) Long travelNoteTag,
            @RequestPart(name = "placeTag", required = false) List<Long> placeTag,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (Objects.isNull(member)) throw new LoginRequiredException("로그인이 필요합니다.");

        boardService.validatePictures(pictures);
        List<String> pictureAddressList = awsS3Service.uploadFiles(
                AWS_S3_BOARD_URI, null, pictures);
        Board board = boardService.createBoard(member, pictureAddressList, text);

        BoardTravelNote boardTravelNote = boardService.createBoardTravelNote(board, travelNoteTag);

        boardService.createBoardPlaces(board, boardTravelNote, placeTag);
        return new BoardIdDto(board.getId());
    }

    @PutMapping("")
    public void editBoard(
            @RequestPart(name = "boardId") Long boardId,
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures,
            @RequestPart(name = "text") String text,
            @RequestPart(name = "travelNoteTag", required = false) Long travelNoteTag,
            @RequestPart(name = "placeTag", required = false) List<Long> placeTag,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Board board = boardService.getBoardById(boardId);

        if (Objects.isNull(member) || !Objects.equals(board.getMember().getId(), member.getId())) {
            throw new BadRequestException("여행 피드를 수정할 수 없습니다.");
        }

        boardService.validatePictures(pictures);
        List<String> pictureAddressList = awsS3Service.uploadFiles(
                AWS_S3_BOARD_URI, null, pictures);
        board.changeImageList(pictureAddressList);
        board.changeText(text);

        boardService.deleteBoardTravelNote(board.getBoardTravelNote());
        BoardTravelNote boardTravelNote = boardService.createBoardTravelNote(board, travelNoteTag);

        boardService.deleteBoardPlaceList(board.getBoardPlaceList());
        boardService.createBoardPlaces(board, boardTravelNote, placeTag);
    }

    @GetMapping("")
    public PageResult<List<BoardFullDto>> callBoards(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
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
    public BoardDto callBoardEditInfo(
            @PathVariable("boardId") Long boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Board board = boardService.getBoardById(boardId);

        if (Objects.isNull(member) || !Objects.equals(board.getMember().getId(), member.getId())) {
            throw new BadRequestException("여행 피드를 수정할 수 없습니다.");
        }

        return new BoardDto(board);
    }

    @GetMapping("/my/{page}/{limit}")
    public PageResult<List<MyBoardDto>> callMyBoards(
            @PathVariable("page") int page,
            @PathVariable("limit") int limit,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (Objects.isNull(member)) throw new LoginRequiredException("로그인이 필요합니다.");

        List<Board> boardList = boardService.getMyBoardList(member, page, limit);

        return new PageResult<>(
                boardList
                        .stream()
                        .map(board -> new MyBoardDto(board, boardCommentService.countCommentByBoard(board)))
                        .toList(),
                boardService.checkNextMyBoardList(member, page, limit));
    }

    @GetMapping("/like/{boardId}")
    public LikeItemDto callBoardLike(
            @PathVariable("boardId") Long boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return boardService.getLikeInfo(boardService.getBoardById(boardId), member);
    }

    @PatchMapping("/like")
    public void changeBoardLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        boardService.changeBoardLike(member, boardService.getBoardById(requestDto.getId()), requestDto.isLike());
    }

}
