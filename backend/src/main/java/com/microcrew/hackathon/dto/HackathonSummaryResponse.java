package com.microcrew.hackathon.dto;

import com.microcrew.hackathon.entity.Hackathon;
import com.microcrew.hackathon.entity.HackathonDifficulty;
import com.microcrew.hackathon.entity.HackathonMode;
import com.microcrew.hackathon.entity.HackathonSkill;
import com.microcrew.hackathon.entity.HackathonStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record HackathonSummaryResponse(
        Long id,
        String name,
        String organizer,
        String bannerUrl,
        OffsetDateTime registrationDeadline,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String location,
        HackathonMode mode,
        Long prizePool,
        Integer teamSizeMin,
        Integer teamSizeMax,
        HackathonDifficulty difficulty,
        HackathonStatus status,
        List<String> skills,
        boolean isSaved
) {
    public static HackathonSummaryResponse fromEntity(Hackathon hackathon, boolean isSaved) {
        List<String> skillNames = hackathon.getSkills() != null
                ? hackathon.getSkills().stream().map(HackathonSkill::getSkillName).sorted().toList()
                : List.of();

        return new HackathonSummaryResponse(
                hackathon.getId(),
                hackathon.getName(),
                hackathon.getOrganizer(),
                hackathon.getBannerUrl(),
                hackathon.getRegistrationDeadline(),
                hackathon.getStartDate(),
                hackathon.getEndDate(),
                hackathon.getLocation(),
                hackathon.getMode(),
                hackathon.getPrizePool(),
                hackathon.getTeamSizeMin(),
                hackathon.getTeamSizeMax(),
                hackathon.getDifficulty(),
                hackathon.getStatus(),
                skillNames,
                isSaved
        );
    }
}
