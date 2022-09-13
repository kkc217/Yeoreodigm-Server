package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.Places;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceStringIdDto {

    private String placeId;

    public PlaceStringIdDto(Places place) {
        this.placeId = place.getId().toString();
    }

}
