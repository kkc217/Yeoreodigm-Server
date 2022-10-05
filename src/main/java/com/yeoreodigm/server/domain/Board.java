package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class)
@SequenceGenerator(
        name = "BOARD_ID_SEQ_GENERATOR",
        sequenceName = "board_id_seq",
        allocationSize = 1)
public class Board {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_ID_SEQ_GENERATOR")
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    private boolean publicShare;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar(50) []")
    private List<String> imageList;

}
