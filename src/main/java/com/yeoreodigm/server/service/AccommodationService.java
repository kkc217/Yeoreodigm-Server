package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Accommodation;
import com.yeoreodigm.server.domain.NearAccommodation;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.accommodation.AccommodationDto;
import com.yeoreodigm.server.dto.accommodation.AccommodationListDto;
import com.yeoreodigm.server.dto.constraint.CacheConst;
import com.yeoreodigm.server.repository.AccommodationRepository;
import com.yeoreodigm.server.repository.NearAccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.domain.AccommodationType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    private final NearAccommodationRepository nearAccommodationRepository;

    @Cacheable(value = CacheConst.NEAR_ACCOMMODATION_ID, key = "'p' + #placeId + 'type' + #type")
    public List<Long> getNearAccommodationId(Long placeId, int type) {
        if (Objects.equals(PENSION.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdPension(placeId);
        } else if (Objects.equals(GUEST_HOUSE.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdGuestHouse(placeId);
        } else if (Objects.equals(HOTEL.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdHotel(placeId);
        } else if (Objects.equals(MINBAK.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdMinbak(placeId);
        } else if (Objects.equals(MOTEL.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdMotel(placeId);
        } else if (Objects.equals(CAMPING.getIndex(), type)) {
            return nearAccommodationRepository.findByPlaceIdCamping(placeId);
        }

        NearAccommodation nearAccommodation = nearAccommodationRepository.findByPlaceId(placeId);
        List<Long> result = new ArrayList<>();
        result.addAll(nearAccommodation.getPension());
        result.addAll(nearAccommodation.getMinbak());
        result.addAll(nearAccommodation.getMotel());
        result.addAll(nearAccommodation.getGuestHouse());
        result.addAll(nearAccommodation.getHotel());
        result.addAll(nearAccommodation.getCamping());
        return result;
    }

    public List<Accommodation> getAccommodationPaging(List<Long> accommodationIdList, int page, int limit) {
        int index = limit * (page - 1);
        limit += index;

        List<Accommodation> result = new ArrayList<>();
        while (index < limit && index < accommodationIdList.size()) {
            result.add(accommodationRepository.findById(accommodationIdList.get(index)));
            index++;
        }

        return result;
    }

    public int checkNextAccommodations(List<Long> accommodationIdList, int page, int limit) {
        return limit * (page - 1) + limit < accommodationIdList.size() ? page + 1 : 0;
    }

    @Cacheable(value = CacheConst.ACCOMMODATION_LIST_DTO, key = "'p' + #lastPlaceId + 'type' + #type + 'page' + #page + 'limit' + #limit")
    public AccommodationListDto getAccommodationListDto(
            int day, int totalDay, Long lastPlaceId, int type, List<Long> accommodationIdList, int page, int limit) {
        return new AccommodationListDto(
                day,
                totalDay,
                new PageResult<>(
                        getAccommodationPaging(accommodationIdList, page, limit)
                                .stream()
                                .map(AccommodationDto::new)
                                .toList(),
                        checkNextAccommodations(accommodationIdList, page, limit)));
    }

}
