package com.yeoreodigm.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "로그인 요청")
public class LoginRequestDto {

    @ApiModelProperty(example = "abc@google.com")
    @NotEmpty
    private String email;

    @ApiModelProperty(example = "qwert1234!@")
    @NotEmpty
    private String password;

}
