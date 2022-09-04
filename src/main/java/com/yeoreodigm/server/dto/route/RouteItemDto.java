package com.yeoreodigm.server.dto.route;

import lombok.Data;

import java.util.List;

@Data
public class RouteItemDto {

    private int day;

    private List<RouteData> routeInfos;

    public RouteItemDto(int day, List<RouteData> routeInfos) {
        this.day = day;
        this.routeInfos = routeInfos;
    }

}
