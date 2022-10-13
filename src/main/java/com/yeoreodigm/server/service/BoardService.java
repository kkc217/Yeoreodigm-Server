package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardPlace;
import com.yeoreodigm.server.domain.board.BoardTravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import com.yeoreodigm.server.repository.board.BoardPlaceRepository;
import com.yeoreodigm.server.repository.board.BoardRepository;
import com.yeoreodigm.server.repository.board.BoardTravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.BoardConst.MAX_NUM_OF_BOARD_PICTURE;
import static com.yeoreodigm.server.dto.constraint.BoardConst.MAX_NUM_OF_BOARD_PLACE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final BoardTravelNoteRepository boardTravelNoteRepository;

    private final BoardPlaceRepository boardPlaceRepository;

    private final TravelNoteRepository travelNoteRepository;

    private final CourseRepository courseRepository;

    private final PlacesRepository placesRepository;


    public Board getBoarById(Long boardId) {
        Board board = boardRepository.findById(boardId);
        if (Objects.isNull(board)) throw new BadRequestException("일치하는 여행 피드를 찾을 수 없습니다.");
        return board;
    }

    @Transactional
    public Board createBoard(Member member, List<String> pictureAddress, String text) {
        Board board = new Board(member, text, pictureAddress);
        boardRepository.saveAndFlush(board);
        return board;
    }

    @Transactional
    public BoardTravelNote createBoardTravelNote(Board board, Long travelNoteId) {
        if (Objects.isNull(travelNoteId)) return null;

        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);

        BoardTravelNote boardTravelNote = new BoardTravelNote(board, travelNote);
        boardTravelNoteRepository.saveAndFlush(boardTravelNote);
        return boardTravelNote;
    }

    @Transactional
    public void createBoardPlaces(
            Board board, BoardTravelNote boardTravelNote, List<Long> placeIdList) {
        if (Objects.isNull(placeIdList) || placeIdList.size() == 0) {
            if (Objects.isNull(boardTravelNote)) return;

            List<Course> courseList
                    = courseRepository.findCoursesByTravelNoteId(boardTravelNote.getTravelNote().getId());

            placeIdList = new ArrayList<>();
            for (int i = 0; i < courseList.size() && placeIdList.size() < MAX_NUM_OF_BOARD_PLACE; i++) {
                for (Long placeId : courseList.get(i).getPlaces()) {
                    placeIdList.add(placeId);
                    if (Objects.equals(MAX_NUM_OF_BOARD_PLACE, placeIdList.size())) break;
                }
            }
        }

        for (Long placeId : placeIdList) {
            Places place = placesRepository.findByPlaceId(placeId);

            BoardPlace boardPlace = new BoardPlace(board, place);
            boardPlaceRepository.save(boardPlace);
        }

        boardPlaceRepository.flush();
    }

    @Transactional
    public void deleteBoardTravelNote(BoardTravelNote boardTravelNote) {
        if (Objects.isNull(boardTravelNote)) return;
        boardTravelNoteRepository.deleteById(boardTravelNote.getId());
    }

    @Transactional
    public void deleteBoardPlace(BoardPlace boardPlace) {
        if (Objects.isNull(boardPlace)) return;
        boardPlaceRepository.deleteById(boardPlace.getId());
    }

    @Transactional
    public void deleteBoardPlaceList(List<BoardPlace> boardPlaceList) {
        if (Objects.isNull(boardPlaceList) || boardPlaceList.size() == 0) return;
        for (BoardPlace boardPlace : boardPlaceList) {
            deleteBoardPlace(boardPlace);
        }
    }

    public void validatePictures(List<MultipartFile> pictures) {
        if (Objects.isNull(pictures) || pictures.size() == 0 || pictures.size() > MAX_NUM_OF_BOARD_PICTURE) {
            throw new BadRequestException("여행 피드 사진은 1장 이상, 10장 이하만 가능합니다.");
        }
    }

}
