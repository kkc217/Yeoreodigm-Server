package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelNote {

    @Id
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

    private LocalDateTime modifiedTime;

    private boolean publicShare;

    private String thumbnail;

    @OneToMany(mappedBy = "travelNote", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    @Builder
    public TravelNote(Long id,
                      Member member,
                      String title,
                      LocalDate dayStart,
                      LocalDate dayEnd,
                      int adult,
                      int child,
                      int animal,
                      List<String> region,
                      List<String> theme,
                      List<Long> placesInput,
                      boolean publicShare,
                      String thumbnail) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.adult = adult;
        this.child = child;
        this.animal = animal;
        this.region = region;
        this.theme = theme;
        this.placesInput = placesInput;
        this.createdTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modifiedTime = this.createdTime;
        this.publicShare = publicShare;
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

    public void changeThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void changeModified(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
