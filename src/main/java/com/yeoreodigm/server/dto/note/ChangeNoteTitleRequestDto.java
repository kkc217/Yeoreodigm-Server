package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class ChangeNoteTitleRequestDto {

    private Long travelNoteId;

    private String newTitle;

}
