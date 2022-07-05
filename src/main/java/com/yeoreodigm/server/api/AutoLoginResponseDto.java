package com.yeoreodigm.server.api;

import com.yeoreodigm.server.domain.Authority;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoLoginResponseDto {

    @ApiModelProperty( example = "abc@google.com" )
    private String email;

    @ApiModelProperty( example = "userA" )
    private String nickname;

    @ApiModelProperty( example = "ROLE_USER" )
    private Authority authority;

}
