package com.yeoreodigm.server.dto.search;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.member.MemberItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class RelatedSearchDto {

    private List<RelatedPlace> places = new ArrayList<>();

    private List<RelatedTravelNote> travelNotes = new ArrayList<>();

    private List<MemberItemDto> members = new ArrayList<>();

    public RelatedSearchDto(
            List<Places> placeList, List<TravelNote> travelNoteList, List<Member> memberList) {
        this.places
                .addAll(
                        placeList
                                .stream()
                                .map(place -> new RelatedPlace(place.getId(), place.getTitle()))
                                .toList());
        this.travelNotes
                .addAll(
                        travelNoteList
                                .stream()
                                .map(travelNote -> new RelatedTravelNote(travelNote.getId(), travelNote.getTitle()))
                                .toList());
        this.members
                .addAll(
                        memberList
                                .stream()
                                .map(MemberItemDto::new)
                                .toList());
    }

    @Getter
    @AllArgsConstructor
    static class RelatedPlace {

        private Long placeId;

        private String title;

    }

    @Getter
    @AllArgsConstructor
    static class RelatedTravelNote {

        private Long travelNoteId;

        private String title;

    }

}
