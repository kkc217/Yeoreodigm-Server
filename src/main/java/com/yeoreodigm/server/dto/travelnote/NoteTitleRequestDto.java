package com.yeoreodigm.server.dto.travelnote;

import lombok.Data;

@Data
public class NoteTitleRequestDto {

    private Long travelNoteId;

    private String newTitle;

}
