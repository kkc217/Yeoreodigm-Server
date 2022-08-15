package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "placeId, memberId"))
public class PlaceLike {

    @Id @GeneratedValue
    private Long id;

    private Long placeId;

    private Long memberId;

    public PlaceLike(Long placeId, Long memberId) {
        this.placeId = placeId;
        this.memberId = memberId;
    }

}
