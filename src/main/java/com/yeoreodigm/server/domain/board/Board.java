package com.yeoreodigm.server.domain.board;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.yeoreodigm.server.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    private boolean publicShare;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar(50) []")
    private List<String> imageList;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL)
    private BoardTravelNote boardTravelNote;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardPlace> boardPlaceList = new ArrayList<>();

    public Board(Long id, Member member, String text, List<String> imageList) {
        this.id = id;
        this.member = member;
        this.text = text;
        this.createdTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modifiedTime = createdTime;
        publicShare = true;
        this.imageList = imageList;
    }

    public void changeImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public void changeText(String text) {
        this.text = text;
    }

}
