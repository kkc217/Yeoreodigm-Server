package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class ChangePublicShareRequestDto {

    private Long travelNoteId;

    private boolean publicShare;

}
