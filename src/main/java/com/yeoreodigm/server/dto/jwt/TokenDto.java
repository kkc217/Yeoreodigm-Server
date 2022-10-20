package com.yeoreodigm.server.dto.jwt;

import lombok.Data;

@Data
public class TokenDto {

    private String accessToken;

    private String refreshToke;

    private String grantType;

    public TokenDto(String accessToken, String refreshToke, String grantType) {
        this.accessToken = accessToken;
        this.refreshToke = refreshToke;
        this.grantType = grantType;
    }

}
