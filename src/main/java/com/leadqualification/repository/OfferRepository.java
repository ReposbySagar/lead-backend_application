package com.leadqualification.repository;

import com.leadqualification.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByName(String name);

    @Query("SELECT o FROM Offer o ORDER BY o.updatedAt DESC LIMIT 1")
    Optional<Offer> findLatestOffer();

    boolean existsByName(String name);
}

