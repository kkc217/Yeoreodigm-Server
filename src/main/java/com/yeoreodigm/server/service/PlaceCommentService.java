package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceComment;
import com.yeoreodigm.server.domain.PlaceCommentLike;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.comment.CommentLikeDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.PlaceCommentLikeRepository;
import com.yeoreodigm.server.repository.PlaceCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceCommentService {

    private final PlaceCommentRepository placeCommentRepository;

    private final PlaceCommentLikeRepository placeCommentLikeRepository;

    @Transactional
    public void addPlaceComment(Member member, Places place, String text) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        PlaceComment placeComment = new PlaceComment(place.getId(), member, text);
        placeCommentRepository.saveAndFlush(placeComment);
    }

    @Transactional
    public void deletePlaceComment(Member member, Long placeCommentId) {
        if (member == null) throw new BadRequestException("댓글을 삭제할 수 없습니다.");

        PlaceComment placeComment = placeCommentRepository.findById(placeCommentId);
        if (placeComment == null) return;

        if (!Objects.equals(member.getId(), placeComment.getMember().getId()))
            throw new BadRequestException("댓글을 삭제할 수 없습니다.");

        placeCommentRepository.deleteById(placeCommentId);
    }

    public List<CommentLikeDto> getPlaceCommentItems(Places place, Member member) {
        List<PlaceComment> placeCommentList = placeCommentRepository.findPlaceCommentsByPlaceId(place.getId());

        List<CommentLikeDto> result = new ArrayList<>();
        for (PlaceComment placeComment : placeCommentList) {
            result.add(new CommentLikeDto(
                    placeComment, getLikeInfo(placeComment.getId(), member)));
        }

        return result;
    }

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
