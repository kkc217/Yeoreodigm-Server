package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "noteCommentId, memberId"))
@SequenceGenerator(
        name = "NOTE_COMMENT_LIKE_ID_SEQ_GENERATOR",
        sequenceName = "note_comment_like_id_seq",
        allocationSize = 1)
public class NoteCommentLike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "NOTE_COMMENT_LIKE_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long noteCommentId;

    private Long memberId;

    public NoteCommentLike(Long noteCommentId, Long memberId) {
        this.noteCommentId = noteCommentId;
        this.memberId = memberId;
    }

}
