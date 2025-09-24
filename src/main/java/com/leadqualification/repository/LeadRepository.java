package com.leadqualification.repository;

import com.leadqualification.entity.IntentLevel;
import com.leadqualification.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByIsScored(Boolean isScored);

    List<Lead> findByIntent(IntentLevel intent);

    List<Lead> findByIsScoredOrderByTotalScoreDesc(Boolean isScored);

    @Query("SELECT l FROM Lead l WHERE l.totalScore >= :minScore ORDER BY l.totalScore DESC")
    List<Lead> findByMinScore(@Param("minScore") Integer minScore);

    @Query("SELECT l FROM Lead l WHERE l.totalScore BETWEEN :minScore AND :maxScore ORDER BY l.totalScore DESC")
    List<Lead> findByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.isScored = true")
    long countScoredLeads();

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.intent = :intent")
    long countByIntent(@Param("intent") IntentLevel intent);

    void deleteAllByIsScored(Boolean isScored);
}

