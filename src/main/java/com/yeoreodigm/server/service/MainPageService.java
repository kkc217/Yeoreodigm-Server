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

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    public MainPageInfoDto getMainPageInfoByMember(Member member) {
        List<MainPageTravelNote> recommendedNotes
                = travelNoteService.getRecommendedNotesMainPage(NUMBER_OF_RECOMMENDED_NOTES, member);

        List<MainPagePlace> recommendedPlaces
                = placeService.getRecommendedPlacesMainPage(NUMBER_OF_RECOMMENDED_PLACES, member);

        List<MainPageTravelNote> weeklyNotes
                = travelNoteService.getWeeklyNotesMainPage(NUMBER_OF_WEEKLY_NOTES, member);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

    public MainPageInfoDto getMainPageInfoGeneral() {
        List<MainPageTravelNote> recommendedNotes
                = travelNoteService.getRandomNotesMainPage(NUMBER_OF_RECOMMENDED_NOTES, null);

        List<MainPagePlace> recommendedPlaces
                = placeService.getRandomPlacesMainPage(NUMBER_OF_RECOMMENDED_PLACES, null);

        List<MainPageTravelNote> weeklyNotes
                = travelNoteService.getWeeklyNotesMainPage(NUMBER_OF_WEEKLY_NOTES, null);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

}
