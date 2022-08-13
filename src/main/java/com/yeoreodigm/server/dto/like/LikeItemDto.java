package com.yeoreodigm.server.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class LikeItemDto {

    private boolean hasLiked;

    private Long likeCount;

}
