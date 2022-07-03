package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String introduction;

    private String profileImage;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime joinDate;

    private boolean optional;

    public Member(String email,
                  String password,
                  String nickname,
                  LocalDate birth,
                  Gender gender,
                  LocalDateTime joinDate,
                  boolean optional
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.joinDate = joinDate;
        this.optional = optional;

        this.introduction = "소개를 입력해주세요.";
        this.profileImage = "defaultImage";
    }

}

