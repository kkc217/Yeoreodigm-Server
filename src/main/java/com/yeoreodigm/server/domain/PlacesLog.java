package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "memberId, placeId"))
@SequenceGenerator(
        name = "PLACE_LOG_ID_SEQ_GENERATOR",
        sequenceName = "place_log_id_seq",
        allocationSize = 1)
public class PlacesLog {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PLACE_LOG_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long placeId;

    private Long memberId;

    private LocalDateTime visitTime;

}
