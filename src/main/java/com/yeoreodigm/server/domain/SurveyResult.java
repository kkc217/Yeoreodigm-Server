package com.yeoreodigm.server.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class SurveyResult {

    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int progress;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar(250) []")
    private List<String> result;

    public SurveyResult(Member member) {
        this.member = member;
        this.progress = 1;
        this.result = new ArrayList<>();
    }

    public void changeProgress(int newProgress) {
        this.progress = newProgress;
    }

    public void addResult(String newResult) {
        this.result.add(newResult);
    }

}
