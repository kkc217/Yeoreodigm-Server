package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "start, goal"))
@SequenceGenerator(
        name = "ROUTE_INFO_ID_SEQ_GENERATOR",
        sequenceName = "route_info_id_seq",
        allocationSize = 1)
public class RouteInfo implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ROUTE_INFO_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long start;

    private Long goal;

    private int distance;

    private int car;

    private int walk;

    public RouteInfo(Long start, Long goal, int distance, int car, int walk) {
        this.start = start;
        this.goal = goal;
        this.distance = distance;
        this.car = car;
        this.walk = walk;
    }

}
