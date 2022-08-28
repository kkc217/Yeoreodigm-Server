package com.yeoreodigm.server.dto.course;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseItemDto {

    private int day;

    private List<PlaceInfo> places = new ArrayList<>();


    public CourseItemDto(int countStart, int day, List<Places> placeList) {
        this.day = day;

        for (int index = 0; index < placeList.size(); index++) {
            Places place = placeList.get(index);
            if (index != placeList.size() - 1) {
                this.places.add(
                        new PlaceInfo(
                                countStart + index,
                                place.getId(),
                                place.getTitle(),
                                place.getImageUrl(),
                                place.getAddress(),
                                false));
            } else {
                this.places.add(
                        new PlaceInfo(
                                countStart + index,
                                place.getId(),
                                place.getTitle(),
                                place.getImageUrl(),
                                place.getAddress(),
                                true));
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

    }

}
