package com.yeoreodigm.server.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchMemberResponseDto {

    private String profileImage;

    private String nickname;

    private String email;

}
