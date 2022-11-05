package com.yeoreodigm.server.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RouteData implements Serializable {

    private String distance;

    private String car;

    private String walk;

    private String searchUrl;

}
