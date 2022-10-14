package com.yeoreodigm.server.domain.board;

import com.yeoreodigm.server.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(indexes = @Index(name = "multiIndex1", columnList = "follower, followee"))
@SequenceGenerator(
        name = "FOLLOW_ID_SEQ_GENERATOR",
        sequenceName = "follow_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Follow(Member follower, Member followee) {
        this.follower = follower;
        this.followee = followee;
    }

}
