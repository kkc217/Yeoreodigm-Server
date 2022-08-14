package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class CompanionRequestDto {

    private Long travelNoteId;

    private Long memberId;

    private String content;
    
}
