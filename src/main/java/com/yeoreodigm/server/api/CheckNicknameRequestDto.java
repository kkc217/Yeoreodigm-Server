package com.yeoreodigm.server.api;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckNicknameRequestDto {
    @NotEmpty
    private String nickname;
}
