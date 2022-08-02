package com.yeoreodigm.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponseDto {

    private String profileImage;

    private String nickname;

    private String email;

}
