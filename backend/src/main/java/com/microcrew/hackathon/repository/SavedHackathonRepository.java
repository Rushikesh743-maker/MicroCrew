package com.microcrew.hackathon.repository;

import com.microcrew.hackathon.entity.SavedHackathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedHackathonRepository extends JpaRepository<SavedHackathon, Long> {

    Optional<SavedHackathon> findByUserIdAndHackathonId(Long userId, Long hackathonId);

    boolean existsByUserIdAndHackathonId(Long userId, Long hackathonId);

    void deleteByUserIdAndHackathonId(Long userId, Long hackathonId);

    @Query("SELECT sh FROM SavedHackathon sh JOIN FETCH sh.hackathon h LEFT JOIN FETCH h.skills WHERE sh.userId = :userId")
    Page<SavedHackathon> findByUserIdWithHackathon(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT sh.hackathonId FROM SavedHackathon sh WHERE sh.userId = :userId AND sh.hackathonId IN :hackathonIds")
    List<Long> findSavedHackathonIds(@Param("userId") Long userId, @Param("hackathonIds") Collection<Long> hackathonIds);
}
