package com.yeoreodigm.server.domain.board;

import com.yeoreodigm.server.domain.Member;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "follower, followee"))
@SequenceGenerator(
        name = "FOLLOW_ID_SEQ_GENERATOR",
        sequenceName = "follow_id_seq",
        allocationSize = 1)
public class Follow {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "FOLLOW_ID_SEQ_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower")
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee")
    private Member followee;

}
