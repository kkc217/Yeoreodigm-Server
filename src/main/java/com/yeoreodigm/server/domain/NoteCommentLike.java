package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "noteCommentId, memberId"))
public class NoteCommentLike {

    @Id @GeneratedValue
    private Long id;

    private Long noteCommentId;

    private Long memberId;

    public NoteCommentLike(Long noteCommentId, Long memberId) {
        this.noteCommentId = noteCommentId;
        this.memberId = memberId;
    }

}
