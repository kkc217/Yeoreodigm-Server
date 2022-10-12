package com.yeoreodigm.server.domain.board;

import com.yeoreodigm.server.domain.Places;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "board_id, place_id"))
@SequenceGenerator(
        name = "BOARD_PLACE_ID_SEQ_GENERATOR",
        sequenceName = "board_place_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPlace {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_PLACE_ID_SEQ_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Places place;

    public BoardPlace(Board board, Places place) {
        this.board = board;
        this.place = place;
    }
}
