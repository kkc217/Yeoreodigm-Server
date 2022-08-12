package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteInfo implements Serializable {

    @Id
    private Long start;

    @Id
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
