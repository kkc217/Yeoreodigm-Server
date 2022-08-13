package com.yeoreodigm.server.domain;

import javax.persistence.*;

@Entity
@Table(indexes = @Index(name = "multiIndex1", columnList = "travelNoteId, memberId"))
public class TravelNoteLike {

    @Id @GeneratedValue
    private Long id;

    private Long travelNoteId;

    private Long memberId;

}
