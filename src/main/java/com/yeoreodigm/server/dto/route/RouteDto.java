package com.yeoreodigm.server.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteDto {

    private int distance;

    private int car;

    private int walk;

}
