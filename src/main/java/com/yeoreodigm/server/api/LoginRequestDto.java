package com.yeoreodigm.server.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequestDto {
    @ApiModelProperty( example = "abc@google.com" )
    @NotEmpty
    private String email;

    @ApiModelProperty( example = "qwert1234!@" )
    @NotEmpty
    private String password;
}
