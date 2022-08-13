package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "memberId, travelNoteId"))
public class TravelNoteLog {

    @Id @GeneratedValue
    private Long id;

    private Long travelNoteId;

    private Long memberId;

    private LocalDateTime visitTime;

    public TravelNoteLog(Long travelNoteId, Long memberId) {
        this.travelNoteId = travelNoteId;
        this.memberId = memberId;
        visitTime = LocalDateTime.now();
    }

    public void updateVisitTime() {
        this.visitTime = LocalDateTime.now();
    }

}
