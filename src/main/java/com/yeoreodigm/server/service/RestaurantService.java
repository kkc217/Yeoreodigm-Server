package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.NearRestaurant;
import com.yeoreodigm.server.domain.Restaurant;
import com.yeoreodigm.server.repository.NearRestaurantRepository;
import com.yeoreodigm.server.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.domain.RestaurantType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final NearRestaurantRepository nearRestaurantRepository;

    public List<Long> getNearRestaurantId(Long placeId, int type) {
        if (Objects.equals(WESTERN.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdWestern(placeId);
        } else if (Objects.equals(CHINESE.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdChinese(placeId);
        } else if (Objects.equals(BUNSIK.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdBunsik(placeId);
        } else if (Objects.equals(CAFE.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdCafe(placeId);
        } else if (Objects.equals(KOREAN.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdKorean(placeId);
        } else if (Objects.equals(JAPANESE.getIndex(), type)) {
            return nearRestaurantRepository.findByPlaceIdJapanese(placeId);
        }

        NearRestaurant nearRestaurant = nearRestaurantRepository.findByPlaceId(placeId);
        List<Long> result = new ArrayList<>();
        result.addAll(nearRestaurant.getWestern());
        result.addAll(nearRestaurant.getChinese());
        result.addAll(nearRestaurant.getBunsik());
        result.addAll(nearRestaurant.getCafe());
        result.addAll(nearRestaurant.getKorean());
        result.addAll(nearRestaurant.getJapanese());
        return result;
    }

    public List<Restaurant> getRestaurantsPaging(List<Long> restaurantIdList, int page, int limit) {
        int index = limit * (page - 1);
        limit += index;

        List<Restaurant> result = new ArrayList<>();
        while (index < limit && index < restaurantIdList.size()) {
            result.add(restaurantRepository.findById(restaurantIdList.get(index)));
            index++;
        }

        return result;
    }

    public int checkNextRestaurants(List<Long> restaurantIdList, int page, int limit) {
        return limit * (page - 1) + limit < restaurantIdList.size() ? page + 1 : 0;
    }

}
