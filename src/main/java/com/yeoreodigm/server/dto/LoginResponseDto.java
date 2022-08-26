package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import lombok.Data;

@Data
public class LoginResponseDto {

    private String email;

    private String nickname;

    private Authority authority;

    private int surveyIndex;

    public LoginResponseDto(final Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.authority = member.getAuthority();
        this.surveyIndex = 0;
    }

}
