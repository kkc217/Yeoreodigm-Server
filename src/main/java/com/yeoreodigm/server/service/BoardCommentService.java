package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.repository.board.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;

    public Long countCommentByBoard(Board board) {
        return boardCommentRepository.countByBoard(board);
    }

}
