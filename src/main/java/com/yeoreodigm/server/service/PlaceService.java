package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.repository.PlaceLikeRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.RouteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceLikeRepository placeLikeRepository;

    private final PlacesRepository placesRepository;

    private final RouteInfoRepository routeInfoRepository;

    private final RouteInfoService routeInfoService;

    public List<Places> searchPlaceLike(Member member, int page, int limit) {
        return placesRepository.findByPlacesIdList(
                placeLikeRepository
                        .findByMemberPaging(member, limit * (page - 1), limit)
                        .stream()
                        .map(PlaceLike::getId)
                        .toList());
    }

    public List<Places> searchPlaces(String content, int page, int limit) {
        return placesRepository.findByTitlePaging(content, limit * (page - 1), limit);
    }

    public int checkNextSearchPage(String content, int page, int limit) {
        return searchPlaces(content, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    public int checkNextLikePage(Member member, int page, int limit) {
        return searchPlaceLike(member, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    public List<Places> searchPlacesByCourse(Course course) {
        return placesRepository.findByPlacesIdList(course.getPlaces());
    }

    @Transactional
    public RouteInfo callRoute(Long start, Long goal) {
        if (start > goal) {
            Long tmp = start;
            start = goal;
            goal = tmp;
        }

        RouteInfo routeInfo = routeInfoRepository.findRouteInfoByPlaces(start, goal);
        if (routeInfo != null) {
            return routeInfo;
        } else if (start.equals(goal)) {
            return new RouteInfo(start, goal, 0, 0, 0);
        } else {
            return routeInfoService.updateRouteInfo(start, goal);
        }
    }

}