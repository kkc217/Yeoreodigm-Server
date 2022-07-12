package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Authority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel(value = "로그인 응답")
public class LoginMemberDto implements Serializable {

    private static final long serialVersionUID = 6249349715519143742L;

    @ApiModelProperty(example = "abc@google.com")
    private String email;

    @ApiModelProperty(example = "userA")
    private String nickname;

    @ApiModelProperty(example = "ROLE_USER")
    private Authority authority;

    private int surveyIndex;

    public LoginMemberDto(String email, String nickname, Authority authority) {
        this.email = email;
        this.nickname = nickname;
        this.authority = authority;
        this.surveyIndex = 0;
    }

}
