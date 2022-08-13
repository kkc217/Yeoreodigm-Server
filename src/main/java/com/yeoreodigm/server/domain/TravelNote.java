package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelNote {

    @Id @GeneratedValue
    @Column(name = "travel_note_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private LocalDate dayStart;

    private LocalDate dayEnd;

    private int adult;

    private int child;

    private int animal;

    @Type(type = "list-array")
    private List<String> region = new ArrayList<>();

    @Type(type = "list-array")
    private List<String> theme = new ArrayList<>();

    @Type(type = "list-array")
    private List<Long> placesInput = new ArrayList<>();

    private LocalDateTime createdTime;

    private LocalDateTime lastModifiedTime;

    private boolean publicShare;

    private String thumbnail;

    @Type(type = "list-array")
    private List<Long> companion = new ArrayList<>();

    @OneToMany(mappedBy = "travelNote", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    @Builder
    public TravelNote(Member member,
                      LocalDate dayStart,
                      LocalDate dayEnd,
                      int adult,
                      int child,
                      int animal,
                      List<String> region,
                      List<String> theme,
                      List<Long> placesInput,
                      String thumbnail) {
        this.title = "Untitled";
        this.member = member;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.adult = adult;
        this.child = child;
        this.animal = animal;
        this.region = region;
        if (theme != null) {
            this.theme = theme;
        }
        if (placesInput != null) {
            this.placesInput = placesInput;
        }
        this.createdTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
        this.publicShare = false;
        this.thumbnail = thumbnail;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeComposition(int adult, int child, int animal) {
        this.adult = adult;
        this.child = child;
        this.animal = animal;
    }

    public void changePublicShare(boolean publicShare) {
        this.publicShare = publicShare;
    }

    public void changeCompanion(List<Long> companion) {
        this.companion = companion;
    }
}
