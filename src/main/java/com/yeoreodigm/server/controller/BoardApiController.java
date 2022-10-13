package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardTravelNote;
import com.yeoreodigm.server.dto.board.BoardDto;
import com.yeoreodigm.server.dto.board.BoardIdDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.AwsS3Service;
import com.yeoreodigm.server.service.BoardService;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BOARD_URI;
import static com.yeoreodigm.server.dto.constraint.BoardConst.MAX_NUM_OF_BOARD_PICTURE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

    private final AwsS3Service awsS3Service;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    @PostMapping("/new")
    public BoardIdDto createBoard(
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures,
            @RequestPart(name = "text") String text,
            @RequestPart(name = "travelNoteTag", required = false) Long travelNoteTag,
            @RequestPart(name = "placeTag", required = false) List<Long> placeTag,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (Objects.isNull(member)) throw new BadRequestException("로그인이 필요합니다.");

        if (Objects.isNull(pictures) || pictures.size() == 0 || pictures.size() > MAX_NUM_OF_BOARD_PICTURE) {
            throw new BadRequestException("여행 피드 사진은 1장 이상, 10장 이하만 가능합니다.");
        }
        List<String> pictureAddressList = awsS3Service.uploadFiles(
                AWS_S3_BOARD_URI, null, pictures);
        Board board = boardService.createBoard(member, pictureAddressList, text);

        BoardTravelNote boardTravelNote = boardService.createBoardTravelNote(board, travelNoteTag);

        boardService.createBoardPlaces(board, boardTravelNote, placeTag);
        return new BoardIdDto(board.getId());
    }

    @GetMapping("/modification/{boardId}")
    public BoardDto editBoard(
            @PathVariable("boardId") Long boardId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Board board = boardService.getBoarById(boardId);

        if (Objects.isNull(member) || !Objects.equals(board.getMember().getId(), member.getId())) {
            throw new BadRequestException("여행 피드를 수정할 수 없습니다.");
        }

        return new BoardDto(board);
    }

}
