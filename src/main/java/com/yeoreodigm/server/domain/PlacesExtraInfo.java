package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class PlacesExtraInfo {

    @Id
    private Long id;

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
