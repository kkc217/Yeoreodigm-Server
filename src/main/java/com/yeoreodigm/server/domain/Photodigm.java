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
        name = "PHOTODIGM_ID_SEQ_GENERATOR",
        sequenceName = "photodigm_id_seq",
        allocationSize = 1)
public class Photodigm {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PHOTODIGM_ID_SEQ_GENERATOR")
    @Column(name = "photodigm_id")
    private Long id;

    private String title;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_id")
    private Frame frame;

    private LocalDateTime created;

    private LocalDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean publicShare;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> pictures;

}
