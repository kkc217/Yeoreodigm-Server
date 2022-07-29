package com.yeoreodigm.server.repository;

import com.yeoreodigm.server.domain.TravelNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class TravelNoteRepository {

    private final EntityManager em;

    public void save(TravelNote travelNote) {
        em.persist(travelNote);
    }

    public void saveAndFlush(TravelNote travelNote) {
        em.persist(travelNote);
        em.flush();
    }

}
