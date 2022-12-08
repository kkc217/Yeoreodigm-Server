package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class PlacesZh implements Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Places place;

    private String title;

    private String address;

    private String introduction;

    private String tag;

}
