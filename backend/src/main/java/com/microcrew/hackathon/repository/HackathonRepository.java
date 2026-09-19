package com.microcrew.hackathon.repository;

import com.microcrew.hackathon.entity.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long>, JpaSpecificationExecutor<Hackathon> {

    @Query("SELECT DISTINCT h FROM Hackathon h LEFT JOIN FETCH h.skills WHERE h.id = :id")
    Optional<Hackathon> findByIdWithSkills(@Param("id") Long id);
}
