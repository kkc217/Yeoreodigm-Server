package com.yeoreodigm.server.api;

import com.yeoreodigm.server.domain.Gender;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
@NoArgsConstructor
public class SaveMemberRequestDto {

        @ApiModelProperty( example = "abc@google.com" )
        @NotEmpty(message = "이메일은 필수입니다.")
        private String email;

        @ApiModelProperty( example = "userA" )
        @NotEmpty(message = "닉네임은 필수입니다.")
        private String nickname;

        @ApiModelProperty( example = "qwert1234!@" )
        @NotEmpty(message = "비밀번호는 필수입니다.")
        private String password;

        @ApiModelProperty( example = "male" )
        private Gender gender;

        @ApiModelProperty( example = "1955" )
        private int year;
        @ApiModelProperty( example = "7" )
        private int month;
        @ApiModelProperty( example = "16" )
        private int day;

        @ApiModelProperty( example = "true" )
        private boolean optional;

}
