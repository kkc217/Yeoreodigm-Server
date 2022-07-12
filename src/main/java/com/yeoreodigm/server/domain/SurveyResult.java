package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyResult {

    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int progress;

    private String result;

    public SurveyResult(Member member) {
        this.member = member;
        this.progress = 1;
        this.result = "";
    }

    public void updateProgress() {
        this.progress++;
    }

    public void changeResult(String result) {
        this.result = result;
    }

}
