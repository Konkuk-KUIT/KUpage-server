package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.Team;
import com.kuit.kupage.domain.teamMatch.TeamApplicant;

import java.util.List;

public record AllTeamsResponse(
        List<TeamIdeaDTO> teams,
        List<Long> appliedTeamIds,
        boolean teamMatchCompleted
) {
    public static AllTeamsResponse from(
            List<Team> teams,
            List<TeamApplicant> teamApplicants,
            boolean teamMatchCompleted
    ) {
        List<TeamIdeaDTO> dtos = teams.stream()
                .map(TeamIdeaDTO::from)
                .toList();
        List<Long> appliedTeamIds = teamApplicants.stream()
                .map(teamApplicant -> teamApplicant.getTeam().getId())
                .toList();
        return new AllTeamsResponse(dtos, appliedTeamIds, teamMatchCompleted);
    }

    public record TeamIdeaDTO(Long teamId, String serviceName, String ownerName, AppType appType, String topicSummary,
                              String imageUrl,
                              String serviceIntroFile, String featureRequirements, String preferredDeveloper) {
        public static TeamIdeaDTO from(Team team) {
            String ownerDisplay = team.getOwnerName() + " - " + team.getBatch().getDescription() + " PM";

            return new TeamIdeaDTO(
                    team.getId(),
                    team.getServiceName(),
                    ownerDisplay,
                    team.getAppType(),
                    team.getTopicSummary(),
                    team.getImageUrl(),
                    team.getServiceIntroFile(),
                    team.getFeatureRequirements(),
                    team.getPreferredDeveloper());
        }
    }
}
