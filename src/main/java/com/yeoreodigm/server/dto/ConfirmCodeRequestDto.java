package com.yeoreodigm.server.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value = "이메일 인증 코드 확인 요청")
public class ConfirmCodeRequestDto {

    @ApiModelProperty( example = "047382" )
    @NotEmpty
    private String confirmCode;

}
