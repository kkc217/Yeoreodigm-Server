package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Gender;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberJoinRequestDto {

        @NotEmpty
        private String email;

        @NotEmpty
        private String nickname;

        @NotEmpty
        private String password;

        private Gender gender;

        private int year;
        private int month;
        private int day;

        private String region;

        private boolean optional;

}
