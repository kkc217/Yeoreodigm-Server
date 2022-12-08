package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "travelNoteId, memberId"))
@SequenceGenerator(
        name = "TRAVEL_NOTE_LIKE_ID_SEQ_GENERATOR",
        sequenceName = "travel_note_like_id_seq",
        allocationSize = 1)
public class TravelNoteLike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "TRAVEL_NOTE_LIKE_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long travelNoteId;

    private Long memberId;

    public TravelNoteLike(Long travelNoteId, Long memberId) {
        this.travelNoteId = travelNoteId;
        this.memberId = memberId;
    }

}
