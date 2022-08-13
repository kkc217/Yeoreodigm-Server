package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.NoteComment;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
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

    public List<CommentItemDto> getNoteCommentInfo(Long travelNoteId, Long memberId) {
        List<NoteComment> noteCommentList = noteCommentRepository.findByTravelNoteID(travelNoteId);

        List<CommentItemDto> result = new ArrayList<>();

        for (NoteComment noteComment : noteCommentList) {
            result.add(new CommentItemDto(
                    noteComment, noteCommentLikeService.getLikeInfo(noteComment.getId(), memberId)));
        }

        return result;
    }

}
