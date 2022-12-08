package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "member_id, travel_note_id"))
@SequenceGenerator(
        name = "COMPANION_ID_SEQ_GENERATOR",
        sequenceName = "companion_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Companion {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COMPANION_ID_SEQ_GENERATOR")
    @Column(name = "companion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_note_id")
    private TravelNote travelNote;

    public Companion(TravelNote travelNote, Member member) {
        this.travelNote = travelNote;
        this.member = member;
    }

}