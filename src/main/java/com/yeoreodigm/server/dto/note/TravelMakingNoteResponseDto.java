package com.yeoreodigm.server.dto.note;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yeoreodigm.server.domain.NoteAuthority;
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
public class TravelMakingNoteResponseDto {

    private NoteAuthority noteAuthority;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dayEnd;

    private int adult;

    private int child;

    private int animal;

    private List<String> region;

    private List<String> theme;

    private boolean publicShare;

    private List<PlacesRecommended> placesRecommended = new ArrayList<>();

    public TravelMakingNoteResponseDto(NoteAuthority noteAuthority, TravelNote travelNote, List<Places> placesList) {
        this.noteAuthority = noteAuthority;
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

    public TravelMakingNoteResponseDto(NoteAuthority noteAuthority, TravelNote travelNote) {
        this.noteAuthority = noteAuthority;
        this.title = travelNote.getTitle();
        this.dayStart = travelNote.getDayStart();
        this.dayEnd = travelNote.getDayEnd();
        this.adult = travelNote.getAdult();
        this.child = travelNote.getChild();
        this.animal = travelNote.getAnimal();
        this.region = travelNote.getRegion();
        this.theme = travelNote.getTheme();
        this.publicShare = travelNote.isPublicShare();
    }

    @Getter
    @AllArgsConstructor
    static class PlacesRecommended {

        private Long placeId;

        private String title;

        private String imageUrl;

    }

}