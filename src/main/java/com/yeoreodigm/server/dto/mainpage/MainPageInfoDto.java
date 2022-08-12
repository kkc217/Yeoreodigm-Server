package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDto {

    private List<MainPageTravelNote> recommendedNotes;

    private List<MainPagePlace> recommendedPlaces;

    private List<MainPageTravelNote> weeklyNotes;

    private List<CoordinateItemInfo> popularPlaces;

    public MainPageInfoDto(
            List<MainPageTravelNote> recommendedNotes,
            List<MainPagePlace> recommendedPlaces,
            List<MainPageTravelNote> weeklyNotes,
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
