package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.NoteCommentLike;
import com.yeoreodigm.server.dto.like.LikeItemDto;
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
        NoteCommentLike noteCommentLike
                = noteCommentLikeRepository.findByNoteCommentIdAndMemberId(noteCommentId, memberId);
        return noteCommentLike != null;
    }

    public LikeItemDto getLikeInfo(Long noteCommentId, Long memberId) {
        return new LikeItemDto(
                checkHasLiked(noteCommentId, memberId),
                countCommentLike(noteCommentId));
    }

}
