package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "travelNoteId, memberId"))
public class TravelNoteLike {

    @Id @GeneratedValue
    private Long id;

    private Long travelNoteId;

    private Long memberId;

    public TravelNoteLike(Long travelNoteId, Long memberId) {
        this.travelNoteId = travelNoteId;
        this.memberId = memberId;
    }

}
