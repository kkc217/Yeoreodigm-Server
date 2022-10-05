package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "board_comment_id, member_id"))
@SequenceGenerator(
        name = "BOARD_COMMENT_LIKE_ID_SEQ_GENERATOR",
        sequenceName = "board_comment_like_id_seq",
        allocationSize = 1)
public class BoardCommentLike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_COMMENT_LIKE_ID_SEQ_GENERATOR")
    @Column(name = "board_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_comment_id")
    private BoardComment boardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
