package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.mainpage.MainPageInfoDto;
import com.yeoreodigm.server.dto.mainpage.MainPageItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yeoreodigm.server.dto.constraint.MainPageConst.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MainPageService {

    private final RecommendService recommendService;

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    public MainPageInfoDto callMainPageMember(Member member) {
        // 노트 추천 API 구현시 수정 예정
        List<MainPageItem> recommendedNotes = travelNoteService.getRecommendedNotes(NUMBER_OF_RECOMMENDED_NOTES);

        List<MainPageItem> recommendedPlaces = recommendService
                .getRecommendedPlaces(member.getId(), null, NUMBER_OF_RECOMMENDED_PLACES)
                .stream()
                .map(MainPageItem::new)
                .toList();

        List<MainPageItem> weeklyNotes = travelNoteService.getWeeklyNotes(NUMBER_OF_WEEKLY_NOTES);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

    public MainPageInfoDto callMainPageVisitor() {
        List<MainPageItem> recommendedNotes = travelNoteService.getRandomNotes(NUMBER_OF_RECOMMENDED_NOTES);

        List<MainPageItem> recommendedPlaces = placeService
                .getRandomPlaces( NUMBER_OF_RECOMMENDED_PLACES)
                .stream()
                .map(MainPageItem::new)
                .toList();

        List<MainPageItem> weeklyNotes = travelNoteService.getWeeklyNotes(NUMBER_OF_WEEKLY_NOTES);

        List<Places> popularPlaces = placeService.getPopularPlaces(NUMBER_OF_POPULAR_PLACES);
        return new MainPageInfoDto(recommendedNotes, recommendedPlaces, weeklyNotes, popularPlaces);
    }

}
