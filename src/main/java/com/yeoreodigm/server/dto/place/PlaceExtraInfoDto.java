package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.PlacesExtraInfo;
import lombok.Data;

@Data
public class PlaceExtraInfoDto {

    private String operatingHours;

    private String fee;

    private String estimatedTime;

    public PlaceExtraInfoDto(PlacesExtraInfo placesExtraInfo) {
        this.operatingHours = placesExtraInfo.getOperatingHours();
        this.fee = placesExtraInfo.getFee();
        this.estimatedTime = placesExtraInfo.getEstimatedTime();
    }

}
