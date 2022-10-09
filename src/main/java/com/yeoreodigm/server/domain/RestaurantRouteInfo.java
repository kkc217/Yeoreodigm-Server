package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "place_id, restaurant_id"))
@SequenceGenerator(
        name = "RESTAURANT_ROUTE_INFO_ID_SEQ_GENERATOR",
        sequenceName = "restaurant_route_info_id_seq",
        allocationSize = 1)
public class RestaurantRouteInfo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "RESTAURANT_ROUTE_INFO_ID_SEQ_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Places place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private int distance;

    private int car;

    private int walk;

    public RestaurantRouteInfo(Places place, Restaurant restaurant, int distance, int car, int walk) {
        this.place = place;
        this.restaurant = restaurant;
        this.distance = distance;
        this.car = car;
        this.walk = walk;
    }

}
