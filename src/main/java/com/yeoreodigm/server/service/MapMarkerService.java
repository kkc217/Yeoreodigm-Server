package com.yeoreodigm.server.service;

import com.yeoreodigm.server.repository.MapMarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapMarkerService {

    private final MapMarkerRepository mapMarkerRepository;

    public List<String> getMarkerColors(int totalDay) {
        return mapMarkerRepository.findMarkerColorsByTotalDay(totalDay);
    }

}
