package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "memberId, travelNoteId"))
@SequenceGenerator(
        name = "TRAVEL_NOTE_LOG_ID_SEQ_GENERATOR",
        sequenceName = "travel_note_log_id_seq",
        allocationSize = 1)
public class TravelNoteLog {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "TRAVEL_NOTE_LOG_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long travelNoteId;

    private Long memberId;

    private LocalDateTime visitTime;

    public TravelNoteLog(TravelNote travelNote, Member member) {
        this.travelNoteId = travelNote.getId();
        this.memberId = member.getId();
        visitTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public TravelNoteLog(Long travelNoteId, Long memberId) {
        this.travelNoteId = travelNoteId;
        this.memberId = memberId;
        visitTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void changeVisitTime(LocalDateTime dateTime) {
        this.visitTime = dateTime;
    }

}
