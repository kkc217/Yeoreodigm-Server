package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "placeId, memberId"))
@SequenceGenerator(
        name = "PLACE_LIKE_ID_SEQ_GENERATOR",
        sequenceName = "place_like_id_seq",
        allocationSize = 1)
public class PlaceLike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PLACE_LIKE_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long placeId;

    private Long memberId;

    public PlaceLike(Long placeId, Long memberId) {
        this.placeId = placeId;
        this.memberId = memberId;
    }

}
