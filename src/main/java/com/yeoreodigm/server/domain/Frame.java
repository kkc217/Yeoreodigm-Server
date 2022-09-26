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
@SequenceGenerator(
        name = "FRAME_ID_SEQ_GENERATOR",
        sequenceName = "frame_id_seq",
        allocationSize = 1)
public class Frame {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "FRAME_ID_SEQ_GENERATOR")
    @Column(name = "frame_id")
    private Long id;

    private String title;

    private String address;

    private int sizeX;

    private int sizeY;

    @Type(type = "list-array")
    @Column(columnDefinition = "integer []")
    private List<Integer> coordinate1;

    @Type(type = "list-array")
    @Column(columnDefinition = "integer []")
    private List<Integer> coordinate2;

    @Type(type = "list-array")
    @Column(columnDefinition = "integer []")
    private List<Integer> coordinate3;

    @Type(type = "list-array")
    @Column(columnDefinition = "integer []")
    private List<Integer> coordinate4;

}
