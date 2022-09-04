package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.yeoreodigm.server.dto.constraint.MemberConst.DEFAULT_PROFILE_IMAGE_URL;

@Entity
@Getter
@AllArgsConstructor
@SequenceGenerator(
        name = "MEMBER_ID_SEQ_GENERATOR",
        sequenceName = "member_id_seq",
        allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_ID_SEQ_GENERATOR"
    )
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

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String region;

    private boolean optional;

    public Member(String email, String password, String nickname,
                  LocalDate birth, Gender gender, String region, boolean optional) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.region = region;
        this.optional = optional;

        this.joinDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.introduction = "소개를 입력해주세요.";
        this.profileImage = DEFAULT_PROFILE_IMAGE_URL;
        this.authority = Authority.ROLE_NOT_PERMITTED;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeAuthority(Authority authority) {
        this.authority = authority;
    }

    public void changeIntroduction(String introduction) {
        this.introduction = introduction;
    }
}

