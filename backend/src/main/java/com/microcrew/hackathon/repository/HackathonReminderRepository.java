package com.microcrew.hackathon.repository;

import com.microcrew.hackathon.entity.HackathonReminder;
import com.microcrew.hackathon.entity.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HackathonReminderRepository extends JpaRepository<HackathonReminder, Long> {

    Optional<HackathonReminder> findByUserIdAndHackathonIdAndReminderType(Long userId, Long hackathonId, ReminderType reminderType);

    boolean existsByUserIdAndHackathonIdAndReminderType(Long userId, Long hackathonId, ReminderType reminderType);

    List<HackathonReminder> findByUserIdAndHackathonId(Long userId, Long hackathonId);

    void deleteByUserIdAndHackathonIdAndReminderType(Long userId, Long hackathonId, ReminderType reminderType);
}
