package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel(value = "로그인 응답")
public class LoginResponseDto {

    @ApiModelProperty(example = "abc@google.com")
    private String email;

    @ApiModelProperty(example = "userA")
    private String nickname;

    @ApiModelProperty(example = "ROLE_USER")
    private Authority authority;

    private int surveyIndex;

    public LoginResponseDto(final Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.authority = member.getAuthority();
        this.surveyIndex = 0;
    }

}
