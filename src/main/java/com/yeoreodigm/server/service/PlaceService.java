package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.LogRepository;
import com.yeoreodigm.server.repository.PlaceLikeRepository;
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

    private final PlaceLikeRepository placeLikeRepository;

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

    public List<PlaceLikeDto> getRecommendedPlaces(int limit, Member member) {
        List<Places> placeList;
        if (member != null) {
            placeList = recommendService.getRecommendedPlaces(member, new ArrayList<>(), limit);
        } else {
            placeList = getRandomPlaces(limit);
        }

        return getMainPageItemList(placeList, member);
    }

    private List<PlaceLikeDto> getMainPageItemList(List<Places> placeList, Member member) {
        List<PlaceLikeDto> result = new ArrayList<>();
        for (Places place : placeList) {
            result.add(new PlaceLikeDto(place, getLikeInfo(place, member)));
        }

        return result;
    }

    public List<PlaceLike> getPlaceLikesByMemberPaging(Member member, int page, int limit) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        return placeLikeRepository
                .findByMemberPaging(member, limit * (page - 1), limit);
    }

    public int checkNextPlaceLikePage(Member member, int page, int limit) {
        List<PlaceLike> placeLikeList = this.getPlaceLikesByMemberPaging(member, page + 1, limit);

        return placeLikeList.size() > 0 ? page + 1 : 0;
    }

    public Long countPlaceLike(Places places) {
        return placeLikeRepository.countByPlaceId(places.getId());
    }

    public boolean checkHasLiked(Places places, Member member) {
        if (member == null) return false;
        return placeLikeRepository.findByPlaceIdAndMemberId(places.getId(), member.getId()) != null;
    }

    public LikeItemDto getLikeInfo(Places place, Member member) {
        return new LikeItemDto(
                checkHasLiked(place, member),
                countPlaceLike(place));
    }

    @Transactional
    public void changePlaceLike(Member member, Long placeId, boolean like) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        PlaceLike placeLike = placeLikeRepository.findByPlaceIdAndMemberId(placeId, member.getId());

        if (like) {
            if (placeLike == null) {
                PlaceLike newPlaceLike = new PlaceLike(placeId, member.getId());
                placeLikeRepository.saveAndFlush(newPlaceLike);
            }
        } else if (placeLike != null) {
            placeLikeRepository.deleteById(placeLike.getId());
        }
    }

}