package com.yeoreodigm.server.dto.jwt;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenMemberInfoDto {

    private String accessToken;

    private String refreshToken;

    private String grantType;

    private Long memberId;

    private String email;

    private String nickname;

    private Authority authority;

    private int surveyIndex;

    public TokenMemberInfoDto(TokenDto tokenDto, Member member) {
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.grantType = tokenDto.getGrantType();

        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.authority = member.getAuthority();
        this.surveyIndex = 0;
    }

}
