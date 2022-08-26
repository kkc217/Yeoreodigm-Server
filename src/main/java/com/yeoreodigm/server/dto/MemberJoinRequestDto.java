package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "회원 가입 요청")
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
