package com.yeoreodigm.server.dto.constraint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvConst {

    public static String PLACE_RECOMMEND_URL;
    public static String PLACE_RECOMMEND_URI;
    public static String COURSE_RECOMMEND_URL;
    public static String COURSE_RECOMMEND_URI;
    public static String COURSE_OPTIMIZE_URL;
    public static String COURSE_OPTIMIZE_URI;
    public static String NOTE_SIMILAR_URL;
    public static String NOTE_SIMILAR_URI;
    public static String NOTE_RECOMMEND_URL;
    public static String NOTE_RECOMMEND_URI;
    public static String PHOTODIGM_URL;
    public static String PHOTODIGM_URI;
    public static String NAVER_API_URL;
    public static String NAVER_API_ID;
    public static String NAVER_API_KEY;
    public static String NAVER_DIRECTION_URI;

    public static final String NAVER_API_ID_NAME = "X-NCP-APIGW-API-KEY-ID";

    public static final String NAVER_API_KEY_NAME = "X-NCP-APIGW-API-KEY";

    @Value("${webclient.place.recommend.url}")
    public void setPlaceRecommendUrl(String placeRecommendUrl) {
        PLACE_RECOMMEND_URL = placeRecommendUrl;
    }

    @Value("${webclient.place.recommend.uri}")
    public void setPlaceRecommendUri(String placeRecommendUri) {
        PLACE_RECOMMEND_URI = placeRecommendUri;
    }

    @Value("${webclient.course.recommend.url}")
    public void setCourseRecommendUrl(String courseRecommendUrl) {
        COURSE_RECOMMEND_URL = courseRecommendUrl;
    }

    @Value("${webclient.course.recommend.uri}")
    public void setCourseRecommendUri(String courseRecommendUri) {
        COURSE_RECOMMEND_URI = courseRecommendUri;
    }

    @Value("${webclient.course.optimize.url}")
    public void setCourseOptimizeUrl(String courseOptimizeUrl) { COURSE_OPTIMIZE_URL = courseOptimizeUrl; }

    @Value("${webclient.course.optimize.uri}")
    public void setCourseOptimizeUri(String courseOptimizeUri) { COURSE_OPTIMIZE_URI = courseOptimizeUri; }

    @Value("${webclient.note.similar.url}")
    public void setNoteSimilarUrl(String noteSimilarUrl) { NOTE_SIMILAR_URL = noteSimilarUrl; }

    @Value("${webclient.note.similar.uri}")
    public void setNoteSimilarUri(String noteSimilarUri) { NOTE_SIMILAR_URI = noteSimilarUri; }

    @Value("${webclient.note.recommend.url}")
    public void setNoteRecommendUrl(String noteRecommendUrl) { NOTE_RECOMMEND_URL = noteRecommendUrl; }

    @Value("${webclient.note.recommend.uri}")
    public void setNoteRecommendUri(String noteRecommendUri) { NOTE_RECOMMEND_URI = noteRecommendUri; }

    @Value("${webclient.photodigm.url}")
    public void setPhotodigmUrl(String photodigmUrl) {
        PHOTODIGM_URL = photodigmUrl;
    }

    @Value("${webclient.photodigm.uri}")
    public void setPhotodigmUri(String photodigmUri) {
        PHOTODIGM_URI = photodigmUri;
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
