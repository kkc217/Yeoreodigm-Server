package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PlaceStringIdDto implements Serializable {

    private String placeId;

    public PlaceStringIdDto(Places place) {
        this.placeId = place.getId().toString();
    }

}
