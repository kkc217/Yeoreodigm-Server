package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteCommentLike;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.NoteCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteCommentLikeService {

    private final NoteCommentLikeRepository noteCommentLikeRepository;

    public Long countCommentLike(Long noteCommentId) {
        return noteCommentLikeRepository.countByNoteCommentId(noteCommentId);
    }

    public boolean checkHasLiked(Long noteCommentId, Long memberId) {
        if (memberId == null) return false;
        return noteCommentLikeRepository.findByNoteCommentIdAndMemberId(noteCommentId, memberId) != null;
    }

    public LikeItemDto getLikeInfo(Long noteCommentId, Long memberId) {
        return new LikeItemDto(
                checkHasLiked(noteCommentId, memberId),
                countCommentLike(noteCommentId));
    }

    @Transactional
    public void changeTravelNoteLike(Member member, Long noteCommentId, boolean like) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

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
