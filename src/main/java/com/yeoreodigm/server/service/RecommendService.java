package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.constraint.EnvConst;
import com.yeoreodigm.server.dto.recommend.RecommendedCoursesDto;
import com.yeoreodigm.server.dto.recommend.RecommendedPlacesDto;
import com.yeoreodigm.server.dto.recommend.RecommendedTravelNoteDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CompanionRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {

    private final PlacesRepository placesRepository;

    private final TravelNoteRepository travelNoteRepository;

    private final CompanionRepository companionRepository;

    private final static int RANDOM_PAGING = 3000;

    public List<List<Long>> getRecommendedCourses(TravelNote travelNote) {
        int day = Period.between(travelNote.getDayStart(), travelNote.getDayEnd()).getDays() + 1;
        StringBuilder include = new StringBuilder();

        List<Long> placeInput = travelNote.getPlacesInput();
        if (placeInput.size() > 0) {
            for (Long placeId : placeInput) {
                include.append(placeId).append(",");
            }
            include.delete(include.length() - 1, include.length());
        } else {
            include.append(0);
        }

        StringBuilder location = new StringBuilder();
        for (String region : travelNote.getRegion()) {
            switch (region) {
                case "제주" -> location.append("east,west,south,north,");
                case "제주 동부" -> location.append("east,");
                case "제주 서부" -> location.append("west,");
                case "제주 남부" -> location.append("south,");
                case "제주 북부" -> location.append("north,");
            }
        }

        WebClient webClient = WebClient.create(EnvConst.COURSE_RECOMMEND_URL);

        Mono<RecommendedCoursesDto> apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.COURSE_RECOMMEND_URI)
                        .queryParam("id", travelNote.getMember().getId())
                        .queryParam("day", day)
                        .queryParam("include", include)
                        .queryParam("location", location.substring(0, location.length() - 1))
                        .build())
                .retrieve()
                .bodyToMono(RecommendedCoursesDto.class);

        try {
            return Objects.requireNonNull(apiResult.block()).getCourseList();
        } catch (WebClientResponseException | NullPointerException e) {
            throw new BadRequestException("추천 코스를 불러오는데 실패하였습니다.");
        }
    }

    public List<Places> getRecommendedPlacesByTravelNote(TravelNote travelNote, int limit) {
        List<Course> courseList = travelNote.getCourses();
        List<Long> placeIdList = new ArrayList<>();
        for (Course course : courseList) {
            List<Long> coursePlaces = course.getPlaces();
            placeIdList.addAll(coursePlaces);
        }

        List<Member> memberList = new ArrayList<>();
        memberList.add(travelNote.getMember());

        List<Companion> companionList = companionRepository.findCompanionsByTravelNote(travelNote);
        memberList.addAll(companionList.stream().map(Companion::getMember).toList());

        return getRecommendedPlaces(memberList, placeIdList, limit);
    }

    public List<Places> getRecommendedPlaces(List<Member> memberList, List<Long> placeIdList, int limit) {
        StringBuilder placeString = new StringBuilder();

        for (Long placeId : placeIdList) {
            placeString.append(placeId).append(",");
        }

        if (placeString.length() == 0) {
            placeString.append("0,");
        }

        StringBuilder memberString = new StringBuilder();

        for (Member member : memberList) {
            memberString.append(member.getId()).append(",");
        }

        WebClient webClient = WebClient.create(EnvConst.PLACE_RECOMMEND_URL);

        Mono<RecommendedPlacesDto> apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.PLACE_RECOMMEND_URI)
                        .queryParam("memberId", memberString.substring(0, memberString.length() - 1))
                        .queryParam("placesInCourse", placeString.substring(0, placeString.length() - 1))
                        .queryParam("numOfResult", limit)
                        .build())
                .retrieve()
                .bodyToMono(RecommendedPlacesDto.class);

        try {
            return Objects.requireNonNull(apiResult.block()).getPlaceList()
                    .stream()
                    .map(placesRepository::findByPlaceId)
                    .toList();
        } catch (WebClientResponseException | NullPointerException e) {
            throw new BadRequestException("추천 여행지를 불러오는데 실패하였습니다.");
        }
    }

    public List<TravelNote> getSimilarTravelNotes(TravelNote travelNote, int limit, Member member) {
        WebClient webClient = WebClient.create(EnvConst.NOTE_SIMILAR_URL);

        Mono<RecommendedTravelNoteDto> apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.NOTE_SIMILAR_URI)
                        .queryParam("travelNoteId", travelNote.getId())
                        .queryParam("numOfResult", limit)
                        .build())
                .retrieve()
                .bodyToMono(RecommendedTravelNoteDto.class);

        try {
            return Objects.requireNonNull(apiResult.block()).getNoteList()
                    .stream()
                    .map(travelNoteRepository::findById)
                    .toList();
        } catch (WebClientResponseException | NullPointerException e) {
            return getRecommendedNotes(limit, member);
        }

    }

    public List<TravelNote> getRecommendedNotes(int limit, Member member) {
        if (member == null) return getRandomNotes(limit);

        WebClient webClient = WebClient.create(EnvConst.NOTE_RECOMMEND_URL);

        Mono<RecommendedTravelNoteDto> apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.NOTE_RECOMMEND_URI)
                        .queryParam("memberId", member.getId())
                        .queryParam("numOfResult", limit)
                        .build())
                .retrieve()
                .bodyToMono(RecommendedTravelNoteDto.class);

        try {
            return Objects.requireNonNull(apiResult.block()).getNoteList()
                    .stream()
                    .map(travelNoteRepository::findById)
                    .toList();
        } catch (WebClientResponseException | NullPointerException e) {
            return getRandomNotes(limit);
        }
    }

    public List<TravelNote> getRandomNotes(int limit) {
        int page = (int) (Math.random() * RANDOM_PAGING);
        return travelNoteRepository.findPublicPagingAndLimiting(page, limit);
    }

}