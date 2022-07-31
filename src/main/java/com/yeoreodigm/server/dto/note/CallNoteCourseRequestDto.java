package com.yeoreodigm.server.dto.note;

import com.yeoreodigm.server.domain.NoteAuthority;
import lombok.Data;

@Data
public class CallNoteCourseRequestDto {

    private Long travelNoteId;

    private NoteAuthority noteAuthority;

}
