package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.constraint.QueryConst;
import com.yeoreodigm.server.repository.PlaceLikeRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
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

    public int checkNextSearchPage(String content, int page) {
        return searchPlaces(content, page + 1, QueryConst.PAGING_LIMIT_PUBLIC).size() > 0 ? page + 1 : 0;
    }

    public int checkNextLikePage(Member member, int page) {
        return searchPlaceLike(member, page + 1, QueryConst.PAGING_LIMIT_PUBLIC).size() > 0 ? page + 1 : 0;
    }

}