package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.NearAccommodation;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QNearAccommodation.nearAccommodation;

@Repository
@RequiredArgsConstructor
public class NearAccommodationRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public NearAccommodation findByPlaceId(Long placeId) {
        try {
            return queryFactory
                    .selectFrom(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdPension(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.pension)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdMinbak(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.minbak)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdMotel(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.motel)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdHotel(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.hotel)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdCamping(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.camping)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

    public List<Long> findByPlaceIdGuestHouse(Long placeId) {
        try {
            return queryFactory
                    .select(nearAccommodation.guestHouse)
                    .from(nearAccommodation)
                    .where(nearAccommodation.placeId.eq(placeId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("근처 숙소가 둘 이상입니다.");
        }
    }

}
