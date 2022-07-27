package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
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

    private String region;

    @Type(type = "list-array")
    private List<String> theme = new ArrayList<>();

    @Type(type = "list-array")
    private List<Long> course = new ArrayList<>();

    @Builder
    public TravelNote(Member member, LocalDate dayStart, LocalDate dayEnd, int adult, int child, int animal, String region, List<String> theme, List<Long> course) {
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
        if (course != null) {
            this.course = course;
        }
    }


}
