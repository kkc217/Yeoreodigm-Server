package com.yeoreodigm.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "닉네임 요청")
public class CheckNicknameRequestDto {

    @ApiModelProperty( example = "userA" )
    @NotEmpty
    private String nickname;

}
