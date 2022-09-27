package com.yeoreodigm.server.dto.course;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.route.RouteData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class CourseRouteDto {

    private int day;

    private boolean hasPrev;

    private boolean hasNext;

    private List<PlaceInfo> places = new ArrayList<>();

    public CourseRouteDto(int countStart, int day, int totalDayCount, List<Places> placeList, List<RouteData> routeInfos) {
        this.day = day;
        this.hasPrev = !Objects.equals(day, 1);
        this.hasNext = !Objects.equals(day, totalDayCount);

        for (int i = 0; i < placeList.size(); i++) {
            Places place = placeList.get(i);
            if (i != placeList.size() - 1) {
                this.places.add(
                        new PlaceInfo(
                                countStart + i,
                                place.getId(),
                                place.getTitle(),
                                place.getImageUrl(),
                                place.getAddress(),
                                false,
                                routeInfos.get(i)));
            } else {
                this.places.add(
                        new PlaceInfo(
                                countStart + i,
                                place.getId(),
                                place.getTitle(),
                                place.getImageUrl(),
                                place.getAddress(),
                                true,
                                null));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    static class PlaceInfo {

        private int index;

        private Long placeId;

        private String title;

        private String imageUrl;

        private String address;

        private boolean hasNext;

        private RouteData routeInfo;

    }

}
