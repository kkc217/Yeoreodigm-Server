package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.Places;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlacesRepository {

    private final EntityManager em;

    public Places findByPlacesId(Long placeId) {
        try {
            return em.createQuery("select p from Places p where p.id = :placeId", Places.class)
                    .setParameter("placeId", placeId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Places> findByPlacesIdList(List<Long> placeIdList) {
        return em.createQuery("select p from Places p where p.id in :placeIdList", Places.class)
                .setParameter("placeIdList", placeIdList)
                .getResultList();
    }

}
