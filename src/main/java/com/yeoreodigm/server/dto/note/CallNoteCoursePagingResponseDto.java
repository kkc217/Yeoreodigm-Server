package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallNoteCoursePagingResponseDto {

    private int day;

    private List<PlaceInfo> places = new ArrayList<>();

    public CallNoteCoursePagingResponseDto(int day, List<Places> placeList) {
        this.day = day;
        for (int idx = 0; idx < placeList.size(); idx++) {
            Places place = placeList.get(idx);
            if (idx != placeList.size() - 1) {
                this.places.add(new PlaceInfo(place.getId(), place.getTitle(), place.getImageUrl(), place.getAddress(), false));
            } else {
                this.places.add(new PlaceInfo(place.getId(), place.getTitle(), place.getImageUrl(), place.getAddress(), true));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    static class PlaceInfo {

        private Long placeId;

        private String title;

        private String imageUrl;

        private String address;

        private boolean hasNext;

    }

}
