package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteService {

    private final TravelNoteRepository travelNoteRepository;

    @Transactional
    public Long submitNotePrepare(TravelNote travelNote) {
        travelNoteRepository.saveAndFlush(travelNote);
        return travelNote.getId();
    }

    public TravelNote callNote(Long id) {
        TravelNote travelNote = travelNoteRepository.findById(id);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 메이킹 노트가 없습니다.");
        }
    }

    public NoteAuthority checkNoteAuthority(Member member, Long travelNoteId) {
        if (member == null) {
            return NoteAuthority.ROLE_VISITOR;
        }

        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);
        if (Objects.equals(travelNote.getMember().getId(), member.getId())) {
            return NoteAuthority.ROLE_OWNER;
        } else {
            return NoteAuthority.ROLE_VISITOR;
        }
        //동행자 추가하면 ROLE_COMPANION 확인하도록 수정
    }

}
