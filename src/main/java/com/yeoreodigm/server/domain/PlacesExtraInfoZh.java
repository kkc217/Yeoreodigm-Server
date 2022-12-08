package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class PlacesExtraInfoZh implements Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Places place;

    private String detailInfo;

    private String extraInfo;

    private String operatingHours;

    private String fee;

    private String indoorOutdoor;

    private String purpose;

    private String amenities;

    private String difficulty;

    private String estimatedTime;

}
