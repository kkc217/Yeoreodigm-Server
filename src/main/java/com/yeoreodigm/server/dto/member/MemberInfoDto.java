package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberInfoDto implements Serializable {

    private Long memberId;

    private String email;

    private String nickname;

    private Authority authority;

    private int surveyIndex;

    public MemberInfoDto(final Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.authority = member.getAuthority();
        this.surveyIndex = 0;
    }

}
