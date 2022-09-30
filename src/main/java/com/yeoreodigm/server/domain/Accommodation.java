package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class)
public class Accommodation {

    @Id
    @Column(name = "accommodation_id")
    private Long id;

    private String title;

    private String address;

    private String introduction;

    private String dialNum;

    @Enumerated(EnumType.STRING)
    private AccommodationType type;

    private String imageUrl;

    private int score;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar(20) []")
    private List<String> tag;

    private double latitude;

    private double longitude;

}
