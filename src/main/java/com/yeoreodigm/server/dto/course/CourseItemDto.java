package com.yeoreodigm.server.dto.course;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_CHILDREN;
import static com.yeoreodigm.server.dto.constraint.PlaceConst.NUMBER_OF_PET;

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
                                place.getLatitude(),
                                place.getLongitude(),
                                place.getImageUrl(),
                                place.getAddress(),
                                Objects.equals(place.getChildren(), NUMBER_OF_CHILDREN),
                                Objects.equals(place.getPet(), NUMBER_OF_PET),
                                false));
            } else {
                this.places.add(
                        new PlaceInfo(
                                countStart + index,
                                place.getId(),
                                place.getTitle(),
                                place.getLatitude(),
                                place.getLongitude(),
                                place.getImageUrl(),
                                place.getAddress(),
                                Objects.equals(place.getChildren(), NUMBER_OF_CHILDREN),
                                Objects.equals(place.getPet(), NUMBER_OF_PET),
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

        private double latitude;

        private double longitude;

        private String imageUrl;

        private String address;

        private boolean child;

        private boolean animal;

        private boolean hasNext;

    }

}
