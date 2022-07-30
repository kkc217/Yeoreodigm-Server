package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CallNoteInfoResponseDto {

    private String title;

    private LocalDate dayStart;

    private LocalDate dayEnd;

    private int adult;

    private int child;

    private int animal;

    private List<String> region;

    private List<String> theme;

    private boolean publicShare;

    private List<PlacesRecommended> placesRecommended = new ArrayList<>();

    public CallNoteInfoResponseDto(TravelNote travelNote, List<Places> placesList) {
        this.title = travelNote.getTitle();
        this.dayStart = travelNote.getDayStart();
        this.dayEnd = travelNote.getDayEnd();
        this.adult = travelNote.getAdult();
        this.child = travelNote.getChild();
        this.animal = travelNote.getAnimal();
        this.region = travelNote.getRegion();
        this.theme = travelNote.getTheme();
        this.publicShare = travelNote.isPublicShare();

        for (Places place : placesList) {
            this.placesRecommended.add(new PlacesRecommended(place.getId(), place.getTitle(), place.getImageUrl()));
        }
    }

    @Getter
    @AllArgsConstructor
    static class PlacesRecommended {

        private Long placeId;

        private String title;

        private String imageUrl;

    }

}