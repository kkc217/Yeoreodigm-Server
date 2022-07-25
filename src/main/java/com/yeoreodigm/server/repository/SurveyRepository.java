package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyItem;
import com.yeoreodigm.server.domain.SurveyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyRepository {

    private final EntityManager em;

    public void save(SurveyResult surveyResult) {
        em.persist(surveyResult);
    }

    public void saveAndFlush(SurveyResult surveyResult) {
        em.persist(surveyResult);
        em.flush();
    }

    public List<SurveyItem> findItemsByGroup(int progress) {
        return em.createQuery("select si from SurveyItem si where si.progress = :progress", SurveyItem.class)
                .setParameter("progress", progress)
                .getResultList();
    }

    public SurveyResult findSurveyResult(Member member) {
        try {
            return em.createQuery("select sr from SurveyResult sr where sr.member = :member", SurveyResult.class)
                    .setParameter("member", member)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
