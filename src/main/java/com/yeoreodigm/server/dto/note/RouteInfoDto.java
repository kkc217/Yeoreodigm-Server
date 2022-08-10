package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.RouteInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class RouteInfoDto {

    private int day;

    private List<RouteInfoData> routeInfos = new ArrayList<>();

    public RouteInfoDto(int day, List<RouteInfo> routeInfoList) {
        this.day = day;

        for (RouteInfo routeInfo : routeInfoList) {
            int distanceInt = routeInfo.getDistance();
            String distance;
            if (distanceInt < 1000) {
                distance = distanceInt + "m 이동";
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
                distance = decimalFormat.format(((float) distanceInt) / 1000) + "km 이동";
            }

            int carInt = routeInfo.getCar() / 60000;
            String car;
            if ((carInt / 60) == 0) {
                car = carInt + "분";
            } else {
                car = (carInt / 60) + "시간 " + (carInt % 60) + "분";
            }

            String walk;
            if (routeInfo.getWalk() < 0) {
                walk = "10시간 이상";
            } else {
                int walkInt = routeInfo.getWalk() / 60000;

                if ((walkInt / 60) == 0) {
                    walk = walkInt + "분";
                } else {
                    walk = (walkInt / 60) + "시간 " + (walkInt % 60) + "분";
                }
            }
            this.routeInfos.add(new RouteInfoData(distance, car, walk));
        }
    }

    @Data
    @AllArgsConstructor
    static class RouteInfoData {
        private String distance;

        private String car;

        private String walk;
    }

}
