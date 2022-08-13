package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "memberId, placeId"))
public class PlacesLog {

    @Id @GeneratedValue
    private Long id;

    private Long placeId;

    private Long memberId;

    private LocalDateTime visitTime;

}
