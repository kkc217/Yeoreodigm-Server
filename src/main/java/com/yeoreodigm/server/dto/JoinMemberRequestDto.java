package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "회원 가입 요청")
public class JoinMemberRequestDto {

        @ApiModelProperty(example = "abc@google.com")
        @NotEmpty
        private String email;

        @ApiModelProperty(example = "userA")
        @NotEmpty
        private String nickname;

        @ApiModelProperty(example = "qwert1234!@")
        @NotEmpty
        private String password;

        @ApiModelProperty(example = "male")
        private Gender gender;

        @ApiModelProperty(example = "1955")
        private int year;
        @ApiModelProperty(example = "7")
        private int month;
        @ApiModelProperty(example = "16")
        private int day;

        @ApiModelProperty(example = "경기도")
        private String region;

        @ApiModelProperty(example = "true")
        private boolean optional;

}
