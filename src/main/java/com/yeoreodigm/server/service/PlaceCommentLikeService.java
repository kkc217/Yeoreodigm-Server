package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceCommentLike;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.PlaceCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceCommentLikeService {

    private final PlaceCommentLikeRepository placeCommentLikeRepository;

    public Long countLike(Long placeCommentId) {
        return placeCommentLikeRepository.countByPlaceCommentId(placeCommentId);
    }

    public boolean checkHasLiked(Long placeCommentId, Member member) {
        if (member == null) return false;
        return placeCommentLikeRepository.findByPlaceCommentIdAndMemberId(placeCommentId, member.getId()) != null;
    }

    public LikeItemDto getLikeInfo(Long placeCommentId, Member member) {
        return new LikeItemDto(
                checkHasLiked(placeCommentId, member),
                countLike(placeCommentId));
    }

    @Transactional
    public void changeLike(Member member, Long placeCommentId, boolean like) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        PlaceCommentLike placeCommentLike
                = placeCommentLikeRepository.findByPlaceCommentIdAndMemberId(placeCommentId, member.getId());

        if (like) {
            if (placeCommentLike == null) {
                PlaceCommentLike newPlaceCommentLike = new PlaceCommentLike(placeCommentId, member.getId());
                placeCommentLikeRepository.saveAndFlush(newPlaceCommentLike);
            }
        } else if (placeCommentLike != null) {
            placeCommentLikeRepository.deleteById(placeCommentLike.getId());
        }
    }

}
