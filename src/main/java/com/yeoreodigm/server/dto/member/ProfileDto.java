package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

@Data
public class ProfileDto {

    private String nickname;

    private String profileImage;

    private String introduction;

    public ProfileDto(Member member) {
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.introduction = member.getIntroduction();
    }

}
