package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
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
    public void submitNotePrepare(TravelNote travelNote) {
        travelNoteRepository.saveAndFlush(travelNote);
    }
}
