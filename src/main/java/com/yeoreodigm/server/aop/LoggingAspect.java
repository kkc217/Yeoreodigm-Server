package com.yeoreodigm.server.aop;

import com.yeoreodigm.server.dto.place.PlaceDetailDto;
import com.yeoreodigm.server.dto.travelnote.NoteDetailInfoResponseDto;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private final PlaceService placeService;

    private final TravelNoteService travelNoteService;

    @AfterReturning(pointcut = "execution(* com.yeoreodigm.server.controller.PlaceDetailApiController.callPlaceDetailInfo(..))", returning = "result")
    public void logAfterReturningCallPlaceDetail(Object result) {
        PlaceDetailDto placeDetailDto = (PlaceDetailDto) result;

        placeService.updateLog(placeDetailDto.getPlaceId(), placeDetailDto.getRequestorId());
    }

    @AfterReturning(pointcut = "execution(* com.yeoreodigm.server.controller.TravelNoteDetailApiController.callTravelNoteDetail(..))", returning = "result")
    public void logAfterReturningCallTravelNoteDetail(Object result) {
        NoteDetailInfoResponseDto noteDetailInfoResponseDto = (NoteDetailInfoResponseDto) result;

        travelNoteService.updateLog(
                noteDetailInfoResponseDto.getTravelNoteId(), noteDetailInfoResponseDto.getRequestorId());
    }

}
