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
@Table(indexes = @Index(name = "multiIndex1", columnList = "placeId"))
public class NearAccommodation {

    @Id
    private Long id;

    private Long placeId;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> pension;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> minbak;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> motel;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> hotel;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> camping;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> guestHouse;

}
