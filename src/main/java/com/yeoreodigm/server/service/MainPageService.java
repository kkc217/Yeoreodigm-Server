package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.mainpage.MainPageInfoDto;
import com.yeoreodigm.server.dto.mainpage.MainPagePlace;
import com.yeoreodigm.server.dto.mainpage.MainPageTravelNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.MainPageConst.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MainPageService {

    private final RecommendService recommendService;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    public MainPageInfoDto getMainPageInfoByMember(Member member) {
        // 노트 추천 API 구현시 수정 예정
        List<MainPageTravelNote> recommendedNotes = travelNoteService.getRecommendedNotes(NUMBER_OF_RECOMMENDED_NOTES);

        List<MainPagePlace> recommendedPlaces = recommendService
                .getRecommendedPlaces(member, new ArrayList<>(), NUMBER_OF_RECOMMENDED_PLACES)
                .stream()
                .map(MainPagePlace::new)
                .toList();

        List<MainPageTravelNote> weeklyNotes = travelNoteService.getWeeklyNotes(NUMBER_OF_WEEKLY_NOTES);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

    public MainPageInfoDto getMainPageInfoGeneral() {
        List<MainPageTravelNote> recommendedNotes = travelNoteService.getRandomNotes(NUMBER_OF_RECOMMENDED_NOTES);

        List<MainPagePlace> recommendedPlaces = placeService
                .getRandomPlaces(NUMBER_OF_RECOMMENDED_PLACES)
                .stream()
                .map(MainPagePlace::new)
                .toList();

        List<MainPageTravelNote> weeklyNotes = travelNoteService.getWeeklyNotes(NUMBER_OF_WEEKLY_NOTES);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

}
