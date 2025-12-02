package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.domain.AppType;
import com.kuit.kupage.domain.teamMatch.Team;

import java.util.List;

public record AllTeamsResponse(
        List<TeamIdeaDTO> teams,
        boolean canApply
) {
    public static AllTeamsResponse from(
            List<Team> teams,
            boolean canApply
    ) {
        List<TeamIdeaDTO> dtos = teams.stream()
                .map(TeamIdeaDTO::from)
                .toList();
        return new AllTeamsResponse(dtos, canApply);
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
