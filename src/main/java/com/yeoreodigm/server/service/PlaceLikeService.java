package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceLike;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.PlaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;

    public List<PlaceLike> getPlaceLikesByMemberPaging(Member member, int page, int limit) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        return placeLikeRepository
                .findByMemberPaging(member, limit * (page - 1), limit);
    }

    public int checkNextPlaceLikePage(Member member, int page, int limit) {
        List<PlaceLike> placeLikeList = this.getPlaceLikesByMemberPaging(member, page + 1, limit);

        return placeLikeList.size() > 0 ? page + 1 : 0;
    }

}
