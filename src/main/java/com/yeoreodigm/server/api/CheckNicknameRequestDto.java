package com.yeoreodigm.server.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckNicknameRequestDto {

    @ApiModelProperty( example = "userA" )
    @NotEmpty
    private String nickname;

}
