package com.yeoreodigm.server.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteData {

    private String distance;

    private String car;

    private String walk;

    private String searchUrl;

}
