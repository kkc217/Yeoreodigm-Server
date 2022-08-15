package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.domain.TravelNoteLike;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.TravelNoteLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteLikeService {

    private final TravelNoteLikeRepository travelNoteLikeRepository;

    public Long countTravelNoteLike(TravelNote travelNote) {
        return travelNoteLikeRepository.countByTravelNoteId(travelNote.getId());
    }

    public boolean checkHasLiked(TravelNote travelNote, Member member) {
        if (member == null) return false;
        return travelNoteLikeRepository.findByTravelNoteIdAndMemberId(travelNote.getId(), member.getId()) != null;
    }

    public LikeItemDto getLikeInfo(TravelNote travelNote, Member member) {
        System.out.println("getLikeInfo");
        System.out.println(member==null);
        return new LikeItemDto(
                checkHasLiked(travelNote, member),
                countTravelNoteLike(travelNote));
    }

    @Transactional
    public void changeTravelNoteLike(Member member, Long travelNoteId, boolean like) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        TravelNoteLike travelNoteLike
                = travelNoteLikeRepository.findByTravelNoteIdAndMemberId(travelNoteId, member.getId());

        if (like) {
            if (travelNoteLike == null) {
                TravelNoteLike newTravelNoteLike = new TravelNoteLike(travelNoteId, member.getId());
                travelNoteLikeRepository.saveAndFlush(newTravelNoteLike);
            }
        } else if (travelNoteLike != null) {
            travelNoteLikeRepository.deleteById(travelNoteLike.getId());
        }
    }

}
