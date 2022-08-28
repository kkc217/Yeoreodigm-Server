package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

@Data
public class MemberItemDto {

    private Long memberId;

    private String profileImage;

    private String nickname;

    private String email;

    public MemberItemDto(Member member) {
        this.memberId = member.getId();
        this.profileImage = member.getProfileImage();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
    }

}
