package com.yeoreodigm.server.dto.jwt;

import lombok.Data;

@Data
public class TokenDto {

    private String accessToken;

    private String refreshToken;

    private String grantType;

    public TokenDto(String accessToken, String refreshToken, String grantType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
    }

}
