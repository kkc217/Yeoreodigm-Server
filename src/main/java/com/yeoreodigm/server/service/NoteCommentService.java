package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteComment;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.NoteCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteCommentService {

    private final NoteCommentRepository noteCommentRepository;

    private final NoteCommentLikeService noteCommentLikeService;

    public List<CommentItemDto> getNoteCommentInfo(TravelNote travelNote, Member member) {
        List<NoteComment> noteCommentList = noteCommentRepository.findByTravelNoteID(travelNote.getId());

        List<CommentItemDto> result = new ArrayList<>();

        for (NoteComment noteComment : noteCommentList) {
            result.add(new CommentItemDto(
                    noteComment, noteCommentLikeService.getLikeInfo(noteComment.getId(), member)));
        }

        return result;
    }

    @Transactional
    public CommentItemDto addNoteComment(Member member, TravelNote travelNote, String text) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        NoteComment noteComment = new NoteComment(travelNote.getId(), member, text);
        noteCommentRepository.saveAndFlush(noteComment);

        return new CommentItemDto(noteComment);
    }

    @Transactional
    public void deleteNoteComment(Member member, Long commentId) {
        if (member == null) throw new BadRequestException("댓글을 삭제할 수 없습니다.");
        NoteComment noteComment = noteCommentRepository.findById(commentId);

        if (noteComment == null) throw new BadRequestException("댓글 삭제를 실패하였습니다.");
        if (!member.getId().equals(noteComment.getMember().getId()))
            throw new BadRequestException("댓글을 삭제할 수 없습니다.");

        noteCommentRepository.deleteById(commentId);
    }
}
