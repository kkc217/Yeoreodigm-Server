package com.yeoreodigm.server.dto.detail.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TravelNoteAndLikeDto {

    private TravelNote travelNote;

    private LikeItemDto likeItemDto;

}
