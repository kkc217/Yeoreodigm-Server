package com.yeoreodigm.server.domain.board;

import com.yeoreodigm.server.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@SequenceGenerator(
        name = "BOARD_COMMENT_ID_SEQ_GENERATOR",
        sequenceName = "board_comment_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public BoardComment(Board board, Member member, String text) {
        this.board = board;
        this.member = member;
        this.text = text;
        this.createdTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modifiedTime = createdTime;
    }

}
