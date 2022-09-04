package com.yeoreodigm.server.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyItem;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.yeoreodigm.server.domain.QSurveyItem.surveyItem;
import static com.yeoreodigm.server.domain.QSurveyResult.surveyResult;

@Repository
@RequiredArgsConstructor
public class SurveyRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public void save(SurveyResult surveyResult) {
        em.persist(surveyResult);
    }

    public void saveAndFlush(SurveyResult surveyResult) {
        save(surveyResult);
        em.flush();
    }

    public List<SurveyItem> findSurveyItemsByProgress(int progress) {
        return queryFactory
                .selectFrom(surveyItem)
                .where(surveyItem.progress.eq(progress))
                .fetch();
    }

    public SurveyResult findSurveyResultByMember(Member member) {
        try {
            return queryFactory
                    .selectFrom(surveyResult)
                    .where(surveyResult.member.eq(member))
                    .fetchOne();
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("일치하는 설문 결과가 둘 이상입니다.");
        }
    }

}
