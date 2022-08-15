package com.yeoreodigm.server.dto.detail.travelnote;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.dto.note.RouteInfoData;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class NoteDetailCourseResponseDto {

    private int day;

    private List<PlaceInfo> places = new ArrayList<>();

    public NoteDetailCourseResponseDto(RouteInfoDto routeInfoDto, List<Places> placeList) {
        this.day = routeInfoDto.getDay();

        List<RouteInfoData> routeInfoDataList = routeInfoDto.getRouteInfos();

        for (int i = 0; i < placeList.size(); i++) {
            Places place = placeList.get(i);
            if (i != placeList.size() - 1) {
                this.places.add(
                        new PlaceInfo(
                                place.getId(),
                                place.getTitle(),
                                place.getAddress(),
                                false,
                                routeInfoDataList.get(i)));
            } else {
                this.places.add(
                        new PlaceInfo(
                                place.getId(),
                                place.getTitle(),
                                place.getAddress(),
                                true,
                                null));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    static class PlaceInfo {

        private Long placeId;

        private String title;

        private String address;

        private boolean hasNext;

        private RouteInfoData routeInfo;

    }

}
