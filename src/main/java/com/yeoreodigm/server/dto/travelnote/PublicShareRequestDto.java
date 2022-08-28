package com.yeoreodigm.server.dto.travelnote;

import lombok.Data;

@Data
public class PublicShareRequestDto {

    private Long travelNoteId;

    private boolean publicShare;

}
