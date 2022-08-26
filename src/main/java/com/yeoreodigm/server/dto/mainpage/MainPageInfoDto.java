package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDto {

    private List<TravelNoteItemDto> recommendedNotes;

    private List<MainPagePlace> recommendedPlaces;

    private List<TravelNoteItemDto> weeklyNotes;

    private List<CoordinateItemInfo> popularPlaces;

    public MainPageInfoDto(
            List<TravelNoteItemDto> recommendedNotes,
            List<MainPagePlace> recommendedPlaces,
            List<TravelNoteItemDto> weeklyNotes,
            List<Places> popularPlaces) {
        this.recommendedNotes = recommendedNotes;
        this.recommendedPlaces = recommendedPlaces;
        this.weeklyNotes = weeklyNotes;
        this.popularPlaces = popularPlaces.stream().map(CoordinateItemInfo::new).toList();
    }


    @Data
    static class CoordinateItemInfo {

        private Long placeId;

        private String title;

        private String imageUrl;

        private Double latitude;

        private Double longitude;

        public CoordinateItemInfo(Places place) {
            this.placeId = place.getId();
            this.title = place.getTitle();
            this.imageUrl = place.getImageUrl();
            this.latitude = place.getLatitude();
            this.longitude = place.getLongitude();
        }

    }

}
