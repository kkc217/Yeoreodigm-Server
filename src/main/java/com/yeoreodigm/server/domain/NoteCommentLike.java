package com.yeoreodigm.server.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = @Index(name = "multiIndex1", columnList = "noteCommentId, memberId"))
public class NoteCommentLike {

    @Id @GeneratedValue
    private Long id;

    private Long noteCommentId;

    private Long memberId;

}
