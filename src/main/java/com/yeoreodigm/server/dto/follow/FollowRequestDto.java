package com.yeoreodigm.server.dto.follow;

import lombok.Data;

@Data
public class FollowRequestDto {

    private Long memberId;

    private boolean follow;

}
