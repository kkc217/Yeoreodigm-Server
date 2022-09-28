package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.yeoreodigm.server.domain.QPicture.*;

@Repository
@AllArgsConstructor
public class PictureRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(Picture picture) {
        em.persist(picture);
    }

    public void flush() {
        em.flush();
    }

    public void saveAndFlush(Picture picture) {
        save(picture);
        flush();
    }

    public Picture findById(Long pictureId) {
        try {
            return queryFactory
                    .selectFrom(picture)
                    .where(picture.id.eq(pictureId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 사진이 둘 이상입니다.");
        }
    }

}
