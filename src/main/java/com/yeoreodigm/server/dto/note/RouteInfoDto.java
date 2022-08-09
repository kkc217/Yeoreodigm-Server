package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.RouteInfo;
import lombok.Data;

import java.text.DecimalFormat;

@Data
public class RouteInfoDto {

    private String distance;

    private String car;

    private String walk;

    public RouteInfoDto(RouteInfo routeInfo) {
        int distance = routeInfo.getDistance();
        if (distance < 1000) {
            this.distance = distance + "m 이동";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
            this.distance = decimalFormat.format(((float) distance) / 1000) + "km 이동";
        }

        int car = routeInfo.getCar() / 60000;
        if ((car / 60) == 0) {
            this.car = car + "분";
        } else {
            this.car = (car / 60) + "시간 " + (car % 60) + "분";
        }

        if (routeInfo.getWalk() < 0) {
            this.walk = "10시간 이상";
        } else {
            int walk = routeInfo.getWalk() / 60000;

            if ((walk / 60) == 0) {
                this.walk = walk + "분";
            } else {
                this.walk = (walk / 60) + "시간 " + (walk % 60) + "분";
            }
        }
    }
}
