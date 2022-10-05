package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "board_id, travel_note_id"))
@SequenceGenerator(
        name = "BOARD_TRAVEL_NOTE_ID_SEQ_GENERATOR",
        sequenceName = "board_travel_note_id_seq",
        allocationSize = 1)
public class BoardTravelNote {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_TRAVEL_NOTE_ID_SEQ_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_note_id")
    private TravelNote travelNote;

}
