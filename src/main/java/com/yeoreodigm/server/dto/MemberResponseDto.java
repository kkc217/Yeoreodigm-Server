package com.yeoreodigm.server.dto;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

@Data
public class MemberResponseDto {

    private String profileImage;

    private String nickname;

    private String email;

    public MemberResponseDto(Member member) {
        this.profileImage = member.getProfileImage();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
    }

}
