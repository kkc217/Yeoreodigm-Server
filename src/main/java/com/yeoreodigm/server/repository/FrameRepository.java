package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QFrame.frame;

@Repository
@RequiredArgsConstructor
public class FrameRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public List<Frame> findAll() {
        return queryFactory
                .selectFrom(frame)
                .fetch();
    }
    
    public Frame findById(Long frameId) {
        try {
            return queryFactory
                    .selectFrom(frame)
                    .where(frame.id.eq(frameId))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 프레임이 둘 이상입니다.");
        }
    }

}
