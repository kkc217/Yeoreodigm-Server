package com.yeoreodigm.server.dto.member;

import com.yeoreodigm.server.domain.Member;
import lombok.Data;

import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.MemberConst.DEFAULT_PROFILE_IMAGE_URL;

@Data
public class ProfileDto {

    private String nickname;

    private String profileImage;

    private boolean defaultProfileImage;

    private String introduction;

    public ProfileDto(Member member) {
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.defaultProfileImage = Objects.equals(member.getProfileImage(), DEFAULT_PROFILE_IMAGE_URL);
        this.introduction = member.getIntroduction();
    }

}
