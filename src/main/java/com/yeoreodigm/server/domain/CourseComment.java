package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "COURSE_COMMENT_ID_SEQ_GENERATOR",
        sequenceName = "course_comment_id_seq",
        allocationSize = 1)
public class CourseComment {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "COURSE_COMMENT_ID_SEQ_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime created;

    private LocalDateTime modified;

    public CourseComment(Course course, Member member, String text) {
        this.course = course;
        this.member = member;
        this.text = text;
        this.created = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modified = this.created;
    }

}
