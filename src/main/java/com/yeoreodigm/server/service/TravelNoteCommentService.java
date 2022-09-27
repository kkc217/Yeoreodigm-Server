package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteComment;
import com.yeoreodigm.server.domain.NoteCommentLike;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.repository.NoteCommentLikeRepository;
import com.yeoreodigm.server.repository.NoteCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteCommentService {

    private final NoteCommentRepository noteCommentRepository;

    private final NoteCommentLikeRepository noteCommentLikeRepository;

    public List<CommentLikeDto> getNoteCommentInfo(TravelNote travelNote, Member member) {
        List<NoteComment> noteCommentList = noteCommentRepository.findByTravelNoteID(travelNote.getId());

        List<CommentLikeDto> result = new ArrayList<>();

        for (NoteComment noteComment : noteCommentList) {
            result.add(new CommentLikeDto(
                    noteComment, getLikeInfo(noteComment.getId(), member)));
        }

        return result;
    }

    @Transactional
    public void addNoteComment(Member member, TravelNote travelNote, String text) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        noteCommentRepository.saveAndFlush(new NoteComment(travelNote.getId(), member, text));
    }

    @Transactional
    public void deleteNoteComment(Member member, Long commentId) {
        if (member == null) throw new BadRequestException("댓글을 삭제할 수 없습니다.");
        NoteComment noteComment = noteCommentRepository.findById(commentId);

        if (noteComment == null) return;
        if (!member.getId().equals(noteComment.getMember().getId()))
            throw new BadRequestException("댓글을 삭제할 수 없습니다.");

        noteCommentRepository.deleteById(commentId);
    }

    public Long countCommentLike(Long noteCommentId) {
        return noteCommentLikeRepository.countByNoteCommentId(noteCommentId);
    }

    public boolean checkHasLiked(Long noteCommentId, Member member) {
        if (member == null) return false;
        return noteCommentLikeRepository.findByNoteCommentIdAndMemberId(noteCommentId, member.getId()) != null;
    }

    public LikeItemDto getLikeInfo(Long noteCommentId, Member member) {
        return new LikeItemDto(
                checkHasLiked(noteCommentId, member),
                countCommentLike(noteCommentId));
    }

    @Transactional
    public void changeTravelNoteLike(Member member, Long noteCommentId, boolean like) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        NoteCommentLike noteCommentLike
                = noteCommentLikeRepository.findByNoteCommentIdAndMemberId(noteCommentId, member.getId());

        if (like) {
            if (noteCommentLike == null) {
                NoteCommentLike newNoteCommentLike = new NoteCommentLike(noteCommentId, member.getId());
                noteCommentLikeRepository.saveAndFlush(newNoteCommentLike);
            }
        } else if (noteCommentLike != null) {
            noteCommentLikeRepository.deleteById(noteCommentLike.getId());
        }
    }

}
