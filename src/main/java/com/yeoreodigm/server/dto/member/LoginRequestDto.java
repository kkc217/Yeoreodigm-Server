package com.yeoreodigm.server.dto.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequestDto {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
