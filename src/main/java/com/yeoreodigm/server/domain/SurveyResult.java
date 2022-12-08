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
@SequenceGenerator(
        name = "SURVEY_RESULT_ID_SEQ_GENERATOR",
        sequenceName = "survey_result_id_seq",
        allocationSize = 1)
public class SurveyResult {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SURVEY_RESULT_ID_SEQ_GENERATOR"
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int progress;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar(250) []")
    private List<Long> result;

    public SurveyResult(Member member) {
        this.member = member;
        this.progress = 1;
        this.result = new ArrayList<>();
    }

    public void changeProgress(int newProgress) {
        this.progress = newProgress;
    }

    public void changeResult(List<Long> result) {
        this.result = result;
    }

}
