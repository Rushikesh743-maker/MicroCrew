package com.microcrew.hackathon.repository;

import com.microcrew.hackathon.entity.HackathonSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonSkillRepository extends JpaRepository<HackathonSkill, Long> {

    List<HackathonSkill> findByHackathonId(Long hackathonId);
}
