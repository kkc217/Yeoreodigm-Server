package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@SequenceGenerator(
        name = "BOARD_COMMENT_ID_SEQ_GENERATOR",
        sequenceName = "board_comment_id_seq",
        allocationSize = 1)
public class BoardComment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "BOARD_COMMENT_ID_SEQ_GENERATOR")
    @Column(name = "board_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

}
