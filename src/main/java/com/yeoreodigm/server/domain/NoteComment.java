package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class NoteComment {

    @Id @GeneratedValue
    @Column(name = "note_comment_id")
    private Long id;

    private Long travel_note_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime created;

    private LocalDateTime modified;

}
