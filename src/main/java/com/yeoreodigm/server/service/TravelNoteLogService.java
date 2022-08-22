package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.domain.TravelNoteLog;
import com.yeoreodigm.server.repository.TravelNoteLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteLogService {

    private final TravelNoteLogRepository travelNoteLogRepository;

    @Transactional
    public void updateTravelNoteLog(TravelNote travelNote, Member member) {
        if (member == null) return;

        TravelNoteLog travelNoteLog
                = travelNoteLogRepository.findByTravelNoteIdAndMemberId(travelNote.getId(), member.getId());

        if (travelNoteLog != null) {
            travelNoteLog.updateVisitTime();
            travelNoteLogRepository.save(travelNoteLog);
            travelNoteLogRepository.flushAndClear();
        } else {
            TravelNoteLog newTravelNoteLog = new TravelNoteLog(travelNote.getId(), member.getId());
            travelNoteLogRepository.save(newTravelNoteLog);
            travelNoteLogRepository.flushAndClear();
        }
    }

}
