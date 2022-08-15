package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceComment {

    @Id @GeneratedValue
    @Column(name = "place_comment_id")
    private Long id;

    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime created;

    private LocalDateTime modified;

    public PlaceComment(Long placeId, Member member, String text) {
        this.placeId = placeId;
        this.member = member;
        this.text = text;
        this.created = LocalDateTime.now();
        this.modified = created;
    }

}
