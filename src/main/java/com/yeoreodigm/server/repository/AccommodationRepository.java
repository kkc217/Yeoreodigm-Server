package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Accommodation;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QAccommodation.accommodation;

@Repository
@RequiredArgsConstructor
public class AccommodationRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public Accommodation findById(Long accommodationId) {
        try {
            return queryFactory
                    .selectFrom(accommodation)
                    .where(accommodation.id.eq(accommodationId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 숙소가 둘 이상입니다.");
        }
    }

}
