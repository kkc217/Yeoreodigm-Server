package com.yeoreodigm.server.dto.constraint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvConst {

    public static String COURSE_RECOMMEND_URL;
    public static String COURSE_RECOMMEND_URI;
    public static String PLACE_RECOMMEND_URL;
    public static String PLACE_RECOMMEND_URI;
    public static String NAVER_API_URL;
    public static String NAVER_API_ID;
    public static String NAVER_API_KEY;
    public static String NAVER_DIRECTION_URI;

    public static final String NAVER_API_ID_NAME = "X-NCP-APIGW-API-KEY-ID";

    public static final String NAVER_API_KEY_NAME = "X-NCP-APIGW-API-KEY";

    @Value("${webclient.course.recommend.url}")
    public void setBaseUrl(String courseRecommendUrl) {
        COURSE_RECOMMEND_URL = courseRecommendUrl;
    }

    @Value("${webclient.course.recommend.uri}")
    public void setCourseRecommendUri(String courseRecommendUri) {
        COURSE_RECOMMEND_URI = courseRecommendUri;
    }

    @Value("${webclient.place.recommend.url}")
    public void setPlaceRecommendUrl(String placeRecommendUrl) {
        PLACE_RECOMMEND_URL = placeRecommendUrl;
    }

    @Value("${webclient.place.recommend.uri}")
    public void setPlaceRecommendUri(String placeRecommendUri) {
        PLACE_RECOMMEND_URI = placeRecommendUri;
    }

    @Value("${webclient.naver.api.url}")
    public void setNaverApiUrl(String naverApiUrl) {
        NAVER_API_URL = naverApiUrl;
    }

    @Value("${webclient.naver.api.id}")
    public void setNaverApiId(String naverApiId) {
        NAVER_API_ID = naverApiId;
    }

    @Value("${webclient.naver.api.key}")
    public void setNaverApiKey(String naverApiKey) {
        NAVER_API_KEY = naverApiKey;
    }

    @Value("${webclient.naver.direction.uri}")
    public void setNaverDirectionUri(String naverDirectionUri) {
        NAVER_DIRECTION_URI = naverDirectionUri;
    }

}
