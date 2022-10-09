package com.yeoreodigm.server.dto.restaurant;

import com.yeoreodigm.server.domain.Restaurant;
import com.yeoreodigm.server.dto.route.RouteData;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantRouteDto {

    private Long restaurantId;

    private String title;

    private String address;

    private String introduction;

    private String dialNum;

    private String type;

    private String imageUrl;

    private int score;

    private List<String> tag;

    private double latitude;

    private double longitude;

    private RouteData routeInfo;

    public RestaurantRouteDto(Restaurant restaurant, RouteData routeInfo) {
        this.restaurantId = restaurant.getId();
        this.title = restaurant.getTitle();
        this.address = restaurant.getAddress();
        this.introduction = restaurant.getIntroduction();
        this.dialNum = restaurant.getDialNum();
        this.type = restaurant.getType().getKName();
        this.imageUrl = restaurant.getImageUrl();
        this.score = restaurant.getScore();
        this.tag = restaurant.getTag();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.routeInfo = routeInfo;
    }

}
