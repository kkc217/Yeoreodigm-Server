package com.yeoreodigm.server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoteComment {

    @Id @GeneratedValue
    @Column(name = "note_comment_id")
    private Long id;

    private Long travelNoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime created;

    private LocalDateTime modified;

    public NoteComment(Long travelNoteId, Member member, String text) {
        this.travelNoteId = travelNoteId;
        this.member = member;
        this.text = text;
        this.created = LocalDateTime.now();
        this.modified = created;
    }
}
