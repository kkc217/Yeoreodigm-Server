package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.NoteCommentLike;
import com.yeoreodigm.server.domain.TravelNoteLike;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.repository.NoteCommentLikeRepository;
import com.yeoreodigm.server.repository.TravelNoteLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteLikeService {

    private final TravelNoteLikeRepository travelNoteLikeRepository;

    public Long countCommentLike(Long travelNoteId) {
        return travelNoteLikeRepository.countByTravelNoteId(travelNoteId);
    }

    public boolean checkHasLiked(Long travelNoteId, Long memberId) {
        if (memberId == null) return false;
        return travelNoteLikeRepository.findByTravelNoteIdAndMemberId(travelNoteId, memberId) != null;
    }

    public LikeItemDto getLikeInfo(Long travelNoteId, Long memberId) {
        return new LikeItemDto(
                checkHasLiked(travelNoteId, memberId),
                countCommentLike(travelNoteId));
    }

}
