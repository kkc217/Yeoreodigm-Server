package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "NOTE_COMMENT_ID_SEQ_GENERATOR",
        sequenceName = "note_comment_id_seq",
        allocationSize = 1)
public class NoteComment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "NOTE_COMMENT_ID_SEQ_GENERATOR"
    )
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
