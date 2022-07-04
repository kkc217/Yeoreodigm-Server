package com.yeoreodigm.server.api;

import com.yeoreodigm.server.domain.Gender;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SaveMemberRequestDto {

        @NotEmpty(message = "이메일은 필수입니다.")
        private String email;

        @NotEmpty(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotEmpty(message = "비밀번호는 필수입니다.")
        private String password;

        private Gender gender;

        private int year;
        private int month;
        private int day;

        private boolean optional;

}
