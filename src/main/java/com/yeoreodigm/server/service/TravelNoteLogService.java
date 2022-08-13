package com.yeoreodigm.server.service;

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
    public void updateTravelNoteLog(Long travelNoteId, Long memberId) {
        TravelNoteLog travelNoteLog = travelNoteLogRepository.findByTravelNoteIdAndMemberId(travelNoteId, memberId);

        if (travelNoteLog != null) {
            travelNoteLog.updateVisitTime();
            travelNoteLogRepository.saveAndFlush(travelNoteLog);
        } else {
            TravelNoteLog newTravelNoteLog = new TravelNoteLog(travelNoteId, memberId);
            travelNoteLogRepository.saveAndFlush(newTravelNoteLog);
        }
    }

}
