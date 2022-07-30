package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TravelNote callNoteInfo(Member member, Long id) {
        TravelNote travelNote = travelNoteRepository.findByMemberAndId(member, id);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 메이킹 노트가 없습니다.");
        }
    }

}
