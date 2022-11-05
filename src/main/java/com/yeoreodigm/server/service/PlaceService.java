package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.place.PlaceDetailDto;
import com.yeoreodigm.server.dto.place.PlaceExtraInfoDto;
import com.yeoreodigm.server.dto.place.PlaceLikeDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.SearchConst.SEARCH_OPTION_LIKE_ASC;
import static com.yeoreodigm.server.dto.constraint.SearchConst.SEARCH_OPTION_LIKE_DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlacesRepository placesRepository;

    private final PlacesEnRepository placesEnRepository;

    private final PlacesZhRepository placesZhRepository;

    private final PlaceLikeRepository placeLikeRepository;

    private final PlacesLogRepository placesLogRepository;

    private final PlacesExtraInfoRepository placesExtraInfoRepository;

    private final PlacesExtraInfoEnRepository placesExtraInfoEnRepository;

    private final PlacesExtraInfoZhRepository placesExtraInfoZhRepository;

    private final LogRepository logRepository;

    private final static int RANDOM_PAGING = 1000;

    public List<Places> getAll() {
        return placesRepository.findAll();
    }

    public Places getPlaceById(Long placeId) {
        Places place = placesRepository.findByPlaceId(placeId);

        if (place != null) {
            return place;
        } else {
            throw new BadRequestException("일치하는 여행지가 없습니다.");
        }
    }

    public PlaceDetailDto getPlaceDetailDto(Member member, Places place, String option) {
        switch (Language.getEnum(option)) {
            case EN -> {
                return new PlaceDetailDto(member, place, placesEnRepository.findByPlaceId(place.getId()));
            }
            case ZH -> {
                return new PlaceDetailDto(member, place, placesZhRepository.findByPlaceId(place.getId()));
            }
            default -> {
                return new PlaceDetailDto(member, place);
            }
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

    public List<Places> searchPlaces(String content, int page, int limit, int option) {
        if (Objects.equals(SEARCH_OPTION_LIKE_ASC, option)) {
            return placesRepository.findByKeywordOrderByLikeAsc(
                    content, limit * (page - 1), limit);
        } else if (Objects.equals(SEARCH_OPTION_LIKE_DESC, option)) {
            return placesRepository.findByKeywordOrderByLikeDesc(
                    content, limit * (page - 1), limit);
        }

        return placesRepository.findPlacesByKeywordPaging(content, limit * (page - 1), limit);
    }

    public int checkNextSearchPage(String content, int page, int limit, int option) {
        return searchPlaces(content, page + 1, limit, option).size() > 0 ? page + 1 : 0;
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

    public List<PlaceLikeDto> getPlaceLikeDtoList(List<Places> placeList, Member member) {
        List<PlaceLikeDto> result = new ArrayList<>();
        for (Places place : placeList) {
            result.add(new PlaceLikeDto(place, getLikeInfo(place, member)));
        }

        return result;
    }

    public List<PlaceLike> getPlaceLikesByMemberPaging(Member member, int page, int limit) {
        if (member == null) return new ArrayList<>();

        return placeLikeRepository
                .findByMemberIdPaging(member.getId(), limit * (page - 1), limit);
    }

    public List<Places> getPlacesOrderByLike(Member member, int page, int limit) {
        if (member == null) return new ArrayList<>();

        return placeLikeRepository.findPlaceIdOrderByLikePaging(
                placeLikeRepository.findPlaceIdByMemberId(member.getId()), limit * (page - 1), limit)
                .stream()
                .map(placesRepository::findByPlaceId)
                .toList();
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

    @Transactional
    public void updateLog(Long placeId, Long memberId) {
        if (Objects.isNull(memberId)) return;

        PlacesLog placeLog = placesLogRepository.findByPlaceAndMember(placeId, memberId);

        if (placeLog == null) {
            placesLogRepository.saveAndFlush(new PlacesLog(placeId, memberId));
        } else {
            placeLog.changeVisitTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            placesLogRepository.saveAndFlush(placeLog);
        }
    }

    public PlacesExtraInfo getPlaceExtraInfo(Places place) {
        return placesExtraInfoRepository.findByPlaceId(place.getId());
    }

    public PlaceExtraInfoDto getPlaceExtraInfoDto(Places place, String option) {
        switch (Language.getEnum(option)) {
            case EN -> {
                return new PlaceExtraInfoDto(placesExtraInfoEnRepository.findByPlaceId(place.getId()));
            }
            case ZH -> {
                return new PlaceExtraInfoDto(placesExtraInfoZhRepository.findByPlaceId(place.getId()));
            }
            default -> {
                return new PlaceExtraInfoDto(placesExtraInfoRepository.findByPlaceId(place.getId()));
            }
        }
    }

}