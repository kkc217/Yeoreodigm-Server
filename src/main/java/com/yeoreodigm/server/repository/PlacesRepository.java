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

    public Places findById(Long id) {
        try {
            return em.createQuery("select p from Places p where p.id = :id", Places.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
