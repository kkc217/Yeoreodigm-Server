package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public PlacesLog(Places place, Member member) {
        this.placeId = place.getId();
        this.memberId = member.getId();
        this.visitTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public PlacesLog(Long placeId, Long memberId) {
        this.placeId = placeId;
        this.memberId = memberId;
        this.visitTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void changeVisitTime(LocalDateTime dateTime) {
        this.visitTime = dateTime;
    }

}
