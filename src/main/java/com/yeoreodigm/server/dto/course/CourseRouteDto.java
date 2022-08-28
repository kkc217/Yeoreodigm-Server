package com.yeoreodigm.server.dto.course;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.route.RouteData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseRouteDto {

    private int day;

    private List<PlaceInfo> places = new ArrayList<>();

    public CourseRouteDto(int countStart, int day, List<Places> placeList, List<RouteData> routeInfos) {
        this.day = day;

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
