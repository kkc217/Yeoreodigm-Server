package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.PlaceComment;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
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

    private final PlaceCommentLikeService placeCommentLikeService;

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

        if (!Objects.equals(member.getId(), placeComment.getMember().getId()))
            throw new BadRequestException("댓글을 삭제할 수 없습니다.");

        placeCommentRepository.deleteById(placeCommentId);
    }

    public List<CommentItemDto> getPlaceCommentItems(Places place, Member member) {
        List<PlaceComment> placeCommentList = placeCommentRepository.findPlaceCommentsByPlaceId(place.getId());

        List<CommentItemDto> result = new ArrayList<>();
        for (PlaceComment placeComment : placeCommentList) {
            result.add(new CommentItemDto(
                    placeComment, placeCommentLikeService.getLikeInfo(placeComment.getId(), member)));
        }

        return result;
    }

}
