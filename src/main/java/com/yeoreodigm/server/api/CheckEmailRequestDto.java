package com.yeoreodigm.server.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckEmailRequestDto {

    @ApiModelProperty( example = "abc@google.com" )
    @NotEmpty
    private String email;

}
