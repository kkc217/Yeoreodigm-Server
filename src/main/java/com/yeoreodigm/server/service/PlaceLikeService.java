package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.repository.PlaceLikeRepository;
import com.yeoreodigm.server.repository.PlacesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceLikeService {

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

    public boolean checkNextPage(Member member, int page) {
        return placeLikeRepository.findByMemberPaging(member, 10 * page).size() != 0;
    }

}
