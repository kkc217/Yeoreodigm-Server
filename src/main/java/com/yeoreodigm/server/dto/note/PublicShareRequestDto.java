package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class PublicShareRequestDto {

    private Long travelNoteId;

    private boolean publicShare;

}
