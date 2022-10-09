package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.Restaurant;
import com.yeoreodigm.server.domain.RestaurantRouteInfo;
import com.yeoreodigm.server.domain.RouteInfo;
import com.yeoreodigm.server.dto.constraint.EnvConst;
import com.yeoreodigm.server.dto.constraint.RouteInfoConst;
import com.yeoreodigm.server.dto.route.RouteData;
import com.yeoreodigm.server.dto.route.RouteDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.PlacesRepository;
import com.yeoreodigm.server.repository.RestaurantRouteRepository;
import com.yeoreodigm.server.repository.RouteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RouteInfoService {

    private final RouteInfoRepository routeInfoRepository;

    private final RestaurantRouteRepository restaurantRouteRepository;

    private final PlacesRepository placesRepository;

    public RouteInfo getRouteInfo(Long startPlaceId, Long goalPlaceId) {
        if (startPlaceId.equals(goalPlaceId)) return new RouteInfo(startPlaceId, goalPlaceId, 0, 0, 0);
        if (startPlaceId > goalPlaceId) {
            Long tmp = startPlaceId;
            startPlaceId = goalPlaceId;
            goalPlaceId = tmp;
        }

        return routeInfoRepository.findRouteInfoByPlaceIds(startPlaceId, goalPlaceId);
    }

    public RestaurantRouteInfo getRestaurantRouteInfo(Places place, Restaurant restaurant) {
        return restaurantRouteRepository.findByIds(place.getId(), restaurant.getId());
    }

    @Transactional
    public RouteInfo updateRouteInfo(Places start, Places goal) {
        RouteDto routeDto = getRouteInfoFromApi(
                start.getLatitude(), start.getLongitude(), goal.getLatitude(), goal.getLongitude());
        RouteInfo routeInfo = new RouteInfo(
                start.getId(), goal.getId(), routeDto.getDistance(), routeDto.getCar(), routeDto.getWalk());
        routeInfoRepository.save(routeInfo);
        routeInfoRepository.flushAndClear();
        return routeInfo;
    }

    @Transactional
    public RestaurantRouteInfo updateRestaurantRouteInfo(Places place, Restaurant restaurant) {
        RouteDto routeDto = getRouteInfoFromApi(
                place.getLatitude(), place.getLongitude(), restaurant.getLatitude(), restaurant.getLongitude());
        RestaurantRouteInfo restaurantRouteInfo = new RestaurantRouteInfo(
                place, restaurant, routeDto.getDistance(), routeDto.getCar(), routeDto.getWalk());
        restaurantRouteRepository.saveAndFlush(restaurantRouteInfo);
        return restaurantRouteInfo;
    }

    private RouteDto getRouteInfoFromApi(
            double startLatitude,
            double startLongitude,
            double goalLatitude,
            double goalLongitude) {
        String startCoordinate = startLongitude + "," + startLatitude;
        String goalCoordinate = goalLongitude + "," + goalLatitude;

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

                return new RouteDto(distance, duration, walk);

            } else if (code == 1) {
                return new RouteDto(0, 0, 0);
            } else {
                throw new BadRequestException("경로 검색에 실패하였습니다.(code: " + code + ")");
            }
        } catch (ParseException e) {
            throw new BadRequestException("API 통신 중 에러가 발생하였습니다. 다시 시도해주시기 바랍니다.");
        }
    }

    public List<RouteData> getRouteDataList(List<RouteInfo> routeInfoList) {
        List<RouteData> result = new ArrayList<>();

        for (RouteInfo routeInfo : routeInfoList) {
            String distance = getDistanceStr(routeInfo.getDistance());

            String car = getCarStr(routeInfo.getCar());

            String walk = getWalkStr(routeInfo.getWalk());

            Places start = placesRepository.findByPlaceId(routeInfo.getStart());
            Places goal = placesRepository.findByPlaceId(routeInfo.getGoal());
            result.add(new RouteData(
                    distance,
                    car,
                    walk,
                    getRouteSearchUrl(
                            start.getTitle(),
                            start.getLatitude(),
                            start.getLongitude(),
                            goal.getTitle(),
                            goal.getLatitude(),
                            goal.getLongitude())));
        }

        return result;
    }

    public RouteData getRouteDataRestaurant(RestaurantRouteInfo restaurantRouteInfo) {
        String distance = getDistanceStr(restaurantRouteInfo.getDistance());
        String car = getCarStr(restaurantRouteInfo.getCar());
        String walk = getWalkStr(restaurantRouteInfo.getWalk());

        Places place = restaurantRouteInfo.getPlace();
        Restaurant restaurant = restaurantRouteInfo.getRestaurant();
        return new RouteData(
                distance,
                car,
                walk,
                getRouteSearchUrl(
                        place.getTitle(),
                        place.getLatitude(),
                        place.getLongitude(),
                        restaurant.getTitle(),
                        restaurant.getLatitude(),
                        restaurant.getLongitude()));
    }

    private String getDistanceStr(int distance) {
        if (distance < 1000) return distance + "m 이동";

        DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
        return decimalFormat.format(((float) distance) / 1000) + "km 이동";
    }

    private String getCarStr(int car) {
        if ((car / 60) == 0) return car + "분";

        return  (car / 60) + "시간 " + (car % 60) + "분";
    }

    private String getWalkStr(int walk) {
        if (walk < 0) return "10시간 이상";

        if ((walk / 60) == 0) return walk + "분";

        return (walk / 60) + "시간 " + (walk % 60) + "분";
    }

    private String getRouteSearchUrl(
            String startTitle,
            double startLatitude,
            double startLongitude,
            String goalTitle,
            double goalLatitude,
            double goalLongitude) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(RouteInfoConst.ROUTE_SEARCH_BASE_URL)
                .append("?menu=route")
                .append("&sname=")
                .append(startTitle)
                .append("&sx=")
                .append(startLongitude)
                .append("&sy=")
                .append(startLatitude)
                .append("&ename=")
                .append(goalTitle)
                .append("&ex=")
                .append(goalLongitude)
                .append("&ey=")
                .append(goalLatitude)
                .append("&pathType=0&showMap=true");

        return stringBuilder.toString();
    }

}
