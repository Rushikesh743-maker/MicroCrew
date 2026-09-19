package com.microcrew.user.repository;

import com.microcrew.user.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    Optional<UserSkill> findByUserIdAndSkillName(Long userId, String skillName);

    Optional<UserSkill> findByUserIdAndSkillNameIgnoreCase(Long userId, String skillName);

    void deleteByUserIdAndSkillName(Long userId, String skillName);

    void deleteByUserId(Long userId);
}
