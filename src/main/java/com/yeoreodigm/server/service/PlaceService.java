package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.place.PlaceItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.LogRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.RouteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlacesRepository placesRepository;

    private final PlaceLikeService placeLikeService;

    private final RouteInfoRepository routeInfoRepository;

    private final RouteInfoService routeInfoService;

    private final LogRepository logRepository;

    private final RecommendService recommendService;

    private final static int RANDOM_PAGING = 1000;

    public Places getPlaceById(Long placeId) {
        Places place = placesRepository.findByPlaceId(placeId);

        if (place != null) {
            return place;
        } else {
            throw new BadRequestException("일치하는 여행지가 없습니다.");
        }
    }

    public List<Places> getPlacesByCourse(Course course) {
        return course.getPlaces()
                .stream()
                .map(this::getPlaceById)
                .toList();
    }

    public List<Places> getPlacesByPlaceLikes(List<PlaceLike> placeLikeList) {
        return placeLikeList
                .stream()
                .map(placeLike -> this.getPlaceById(placeLike.getPlaceId()))
                .toList();
    }

    public List<Places> searchPlaces(String content, int page, int limit) {
        return placesRepository.findPlacesByKeywordPaging(content, limit * (page - 1), limit);
    }

    public int checkNextSearchPage(String content, int page, int limit) {
        return searchPlaces(content, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public RouteInfo getRouteInfo(Long start, Long goal) {
        if (start.equals(goal)) return new RouteInfo(start, goal, 0, 0, 0);
        if (start > goal) {
            Long tmp = start;
            start = goal;
            goal = tmp;
        }

        RouteInfo routeInfo = routeInfoRepository.findRouteInfoByPlaceIds(start, goal);
        if (routeInfo != null) {
            return routeInfo;
        } else {
            return routeInfoService.updateRouteInfo(start, goal);
        }
    }

    public List<Places> getPopularPlaces(int limit) {
        return logRepository
                .findMostPlaceIdLimiting(limit)
                .stream()
                .map(placesRepository::findByPlaceId)
                .toList();
    }

    public List<Places> getRandomPlaces(int limit) {
        int page = (int) (Math.random() * RANDOM_PAGING);
        return placesRepository.findPagingAndLimiting(page, limit);
    }

    public String getRandomImageUrl() {
        return placesRepository.findOneImageUrl((int) (Math.random() * 1000));
    }

    public List<PlaceItemDto> getRecommendedPlaces(int limit, Member member) {
        List<Places> placeList;
        if (member != null) {
            placeList = recommendService.getRecommendedPlaces(member, new ArrayList<>(), limit);
        } else {
            placeList = getRandomPlaces(limit);
        }

        return getMainPageItemList(placeList, member);
    }

    private List<PlaceItemDto> getMainPageItemList(List<Places> placeList, Member member) {
        List<PlaceItemDto> result = new ArrayList<>();
        for (Places place : placeList) {
            result.add(new PlaceItemDto(place, placeLikeService.getLikeInfo(place, member)));
        }

        return result;
    }

}