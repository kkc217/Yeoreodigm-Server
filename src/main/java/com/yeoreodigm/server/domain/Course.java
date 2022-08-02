package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class Course {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_note_id")
    private TravelNote travelNote;

    private int day;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> places;

    public Course(TravelNote travelNote, int day, List<Long> places) {
        this.travelNote = travelNote;
        this.day = day;
        this.places = places;
    }

    public void changePlaces(List<Long> places) {
        this.places = places;
    }

}