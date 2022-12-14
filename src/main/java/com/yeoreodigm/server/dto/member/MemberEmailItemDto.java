package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

@Data
public class MemberEmailItemDto {

    private Long memberId;

    private String profileImage;

    private String nickname;

    private String email;

    public MemberEmailItemDto(Member member) {
        this.memberId = member.getId();
        this.profileImage = member.getProfileImage();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
    }

}
