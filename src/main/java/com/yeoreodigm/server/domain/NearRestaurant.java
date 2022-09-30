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
public class NearRestaurant {

    @Id
    private Long id;

    private Long placeId;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> korean;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> chinese;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> japanese;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> western;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> bunsik;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> cafe;

}
