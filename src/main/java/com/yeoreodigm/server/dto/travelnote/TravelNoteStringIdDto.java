package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TravelNoteStringIdDto {

    private String travelNoteId;

    public TravelNoteStringIdDto(TravelNote travelNote) {
        this.travelNoteId = travelNote.getId().toString();
    }

}
