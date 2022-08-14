package com.yeoreodigm.server.dto.note;

import lombok.Data;

@Data
public class NoteCompositionRequestDto {

    private Long travelNoteId;

    private int adult;

    private int child;

    private int animal;

}
