package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.SurveyItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyRepository {

    private final EntityManager em;

    public List<SurveyItem> findByGroup(int group) {
        return em.createQuery("select s from SurveyItem s where s.group = :group", SurveyItem.class)
                .setParameter("group", group)
                .getResultList();
    }

}
