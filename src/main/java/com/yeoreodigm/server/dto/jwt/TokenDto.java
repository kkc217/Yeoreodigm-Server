package com.yeoreodigm.server.dto.jwt;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
