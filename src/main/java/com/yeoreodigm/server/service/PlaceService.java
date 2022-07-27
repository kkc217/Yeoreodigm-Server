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

    public List<Places> searchPlaceLike(Member member, int page) {
        return placesRepository.findByPlacesIdList(
                placeLikeRepository
                        .findByMemberPaging(member, 10 * (page - 1))
                        .stream()
                        .map(PlaceLike::getId)
                        .toList());
    }

    public List<Places> searchPlaces(String content, int page) {
        return placesRepository.findByTitlePaging(content, 10 * (page - 1), QueryConst.PAGING_LIMIT);
    }

    public int checkNextSearchPlaces(String content, int page) {
        return searchPlaces(content, page + 1).size() > 0 ? page + 1 : 0;
    }

    public int checkNextPage(Member member, int page) {
        return placeLikeRepository.findByMemberPaging(member, 10 * page).size() > 0 ? page + 1 : 0;
    }

}
