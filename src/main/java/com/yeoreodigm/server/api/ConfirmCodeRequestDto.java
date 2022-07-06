package com.yeoreodigm.server.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ConfirmCodeRequestDto {

    @ApiModelProperty( example = "047382" )
    @NotEmpty
    private String confirmCode;

}
