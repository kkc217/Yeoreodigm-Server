package com.yeoreodigm.server.dto.place;

import com.yeoreodigm.server.domain.PlacesExtraInfo;
import com.yeoreodigm.server.domain.PlacesExtraInfoEn;
import com.yeoreodigm.server.domain.PlacesExtraInfoZh;
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

    public PlaceExtraInfoDto(PlacesExtraInfoEn placesExtraInfoEn) {
        this.operatingHours = placesExtraInfoEn.getOperatingHours();
        this.fee = placesExtraInfoEn.getFee();
        this.estimatedTime = placesExtraInfoEn.getEstimatedTime();
    }

    public PlaceExtraInfoDto(PlacesExtraInfoZh placesExtraInfoZh) {
        this.operatingHours = placesExtraInfoZh.getOperatingHours();
        this.fee = placesExtraInfoZh.getFee();
        this.estimatedTime = placesExtraInfoZh.getEstimatedTime();
    }

}
