package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.PhotodigmConst.PHOTODIGM_NUMBER_OF_PICTURE;

@Entity
@Getter
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class)
@SequenceGenerator(
        name = "PHOTODIGM_ID_SEQ_GENERATOR",
        sequenceName = "photodigm_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photodigm {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PHOTODIGM_ID_SEQ_GENERATOR")
    @Column(name = "photodigm_id")
    private Long id;

    private String title;

    private String address;

    private Long frameId;

    private LocalDateTime created;

    private LocalDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Type(type = "list-array")
    @Column(columnDefinition = "bigint []")
    private List<Long> pictures = new ArrayList<>();

    @Builder
    public Photodigm(
            String title,
            String address,
            Long frameId,
            Member member) {
        this.title = title;
        this.address = address;
        this.frameId = frameId;
        this.created = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modified = this.created;
        this.member = member;
        for (int i = 0; i < PHOTODIGM_NUMBER_OF_PICTURE; i++) {
            this.pictures.add(null);
        }
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changePictures(List<Long> pictureList) {
        this.pictures = pictureList;
    }

    public void changeFrame(Long frameId) {
        this.frameId = frameId;
    }
}
