package com.yeoreodigm.server.aop;

import com.yeoreodigm.server.dto.place.PlaceDetailDto;
import com.yeoreodigm.server.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private final PlaceService placeService;

    @AfterReturning(pointcut = "execution(* com.yeoreodigm.server.controller.PlaceDetailApiController.callPlaceDetailInfo(..))", returning = "result")
    public void logAfterReturningCallPlaceDetail(Object result) {
        PlaceDetailDto placeDetailDto = (PlaceDetailDto) result;

        placeService.updateLog(placeDetailDto.getPlaceId(), placeDetailDto.getRequestorId());
    }

}
