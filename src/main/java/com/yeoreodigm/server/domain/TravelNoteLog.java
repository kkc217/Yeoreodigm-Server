package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
public class TravelNoteLog {

    @Id @GeneratedValue
    private Long id;

    private Long memberId;

    private Long travelNoteId;

    private LocalDateTime visitTime;

}
