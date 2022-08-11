package com.yeoreodigm.server.dto.mainpage;

import com.yeoreodigm.server.domain.Places;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDto {

    private List<MainPageItem> recommendedNotes;

    private List<MainPageItem> recommendedPlaces;

    private List<MainPageItem> weeklyNotes;

    private List<CoordinateItemInfo> popularPlaces;

    public MainPageInfoDto(
            List<MainPageItem> recommendedNotes,
            List<MainPageItem> recommendedPlaces,
            List<MainPageItem> weeklyNotes,
            List<Places> popularPlaces) {
        this.recommendedNotes = recommendedNotes;
        this.recommendedPlaces = recommendedPlaces;
        this.weeklyNotes = weeklyNotes;
        this.popularPlaces = popularPlaces.stream().map(CoordinateItemInfo::new).toList();
    }


    @Data
    static class CoordinateItemInfo {

        private Long id;

        private String title;

        private String imageUrl;

        private Double latitude;

        private Double longitude;

        public CoordinateItemInfo(Places place) {
            this.id = place.getId();
            this.title = place.getTitle();
            this.imageUrl = place.getImageUrl();
            this.latitude = place.getLatitude();
            this.longitude = place.getLongitude();
        }

    }

}
