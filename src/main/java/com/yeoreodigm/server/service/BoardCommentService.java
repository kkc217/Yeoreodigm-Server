package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.board.Board;
import com.yeoreodigm.server.domain.board.BoardComment;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.repository.board.BoardCommentLikeRepository;
import com.yeoreodigm.server.repository.board.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;

    private final BoardCommentLikeRepository boardCommentLikeRepository;

    public List<BoardComment> getBoardCommentsByBoard(Board board) {
        return boardCommentRepository.findByBoardId(board.getId());
    }

    public Long countCommentByBoard(Board board) {
        return boardCommentRepository.countByBoard(board);
    }

    public boolean checkHasLiked(BoardComment boardComment, Member member) {
        if (member == null) return false;
        return boardCommentLikeRepository.findByBoardCommentIdAndMemberId(boardComment.getId(), member.getId()) != null;
    }

    public Long countCommentLike(BoardComment boardComment) {
        return boardCommentLikeRepository.countByBoardCommentId(boardComment.getId());
    }

    public LikeItemDto getLikeInfo(BoardComment boardComment, Member member) {
        return new LikeItemDto(checkHasLiked(boardComment, member), countCommentLike(boardComment));
    }

    @Transactional
    public void addBoardComment(Member member, Board board, String text) {
        if (Objects.isNull(member)) throw new LoginRequiredException("로그인이 필요합니다.");

        boardCommentRepository.saveAndFlush(new BoardComment(board, member, text));
    }

}