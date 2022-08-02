package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallNoteCourseResponseDto {

    private int day;

    private List<Coordinate> places = new ArrayList<>();

    public CallNoteCourseResponseDto(int day, List<Places> placeList) {
        this.day = day;
        for (Places place : placeList) {
            places.add(new Coordinate(
                    place.getId(),
                    place.getTitle(),
                    place.getImageUrl(),
                    place.getAddress(),
                    place.getLatitude(),
                    place.getLongitude()
            ));
        }
    }

    @Getter
    @AllArgsConstructor
    static class Coordinate {

        private Long placeId;

        private String title;

        private String imageUrl;

        private String address;

        private float latitude;

        private float longitude;

    }

}
