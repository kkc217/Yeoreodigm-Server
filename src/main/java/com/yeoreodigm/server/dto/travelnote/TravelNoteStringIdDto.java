package com.yeoreodigm.server.dto.travelnote;

import com.yeoreodigm.server.domain.TravelNote;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TravelNoteStringIdDto implements Serializable {

    private String travelNoteId;

    public TravelNoteStringIdDto(TravelNote travelNote) {
        this.travelNoteId = travelNote.getId().toString();
    }

}
