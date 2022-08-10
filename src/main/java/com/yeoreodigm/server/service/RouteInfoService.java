package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.dto.constraint.EnvConst;
import com.yeoreodigm.server.dto.constraint.RouteInfoConst;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.RouteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RouteInfoService {

    private final RouteInfoRepository routeInfoRepository;

    private final PlacesRepository placesRepository;

    @Transactional
    public RouteInfo updateRouteInfo(Long start, Long goal) {
        RouteInfo routeInfo = getRouteInfoFromApi(start, goal);
        routeInfoRepository.saveAndFlush(routeInfo);
        return routeInfo;
    }

    private RouteInfo getRouteInfoFromApi(Long start, Long goal) {
        Places startPlace = placesRepository.findByPlacesId(start);
        Places goalPlace = placesRepository.findByPlacesId(goal);

        String startCoordinate = startPlace.getLongitude() + "," + startPlace.getLatitude();
        String goalCoordinate = goalPlace.getLongitude() + "," + goalPlace.getLatitude();

        WebClient webClient = WebClient.create(EnvConst.NAVER_API_URL);

        String apiResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(EnvConst.NAVER_DIRECTION_URI)
                        .queryParam("start", startCoordinate)
                        .queryParam("goal", goalCoordinate)
                        .queryParam(EnvConst.NAVER_API_ID_NAME, EnvConst.NAVER_API_ID)
                        .queryParam(EnvConst.NAVER_API_KEY_NAME, EnvConst.NAVER_API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);

            int code = Integer.parseInt(jsonObject.get("code").toString());

            if (code == 0) {

                JSONObject summary = (JSONObject) ((JSONObject) ((JSONArray) ((JSONObject)
                        jsonObject
                        .get("route"))
                        .get("traoptimal"))
                        .get(0))
                        .get("summary");

                int distance = Integer.parseInt(summary.get("distance").toString());
                int duration =
                        Integer.parseInt(summary.get("duration").toString()) / RouteInfoConst.MILLISECOND_TO_MINUTE;

                int walk;
                if (distance > 40000) {
                    walk = -1;
                } else {
                    walk = (int) (((float) distance / 1000) * RouteInfoConst.WALKING_MINUTE_PER_KILOMETER);
                }

                return new RouteInfo(start, goal, distance, duration, walk);

            } else {
                throw new BadRequestException("경로 검색에 실패하였습니다.");
            }
        } catch (ParseException e) {
            throw new BadRequestException("API 통신 중 에러가 발생하였습니다. 다시 시도해주시기 바랍니다.");
        }
    }

}
