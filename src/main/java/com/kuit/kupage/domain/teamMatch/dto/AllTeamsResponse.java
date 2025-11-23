package com.kuit.kupage.domain.teamMatch.dto;

import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.Team;

import java.util.List;

public record AllTeamsResponse(List<TeamIdeaDTO> teams) {
    public static AllTeamsResponse from(List<Team> teams) {
        List<TeamIdeaDTO> dtos = teams.stream()
                .map(TeamIdeaDTO::from)
                .toList();
        return new AllTeamsResponse(dtos);
    }

    public record TeamIdeaDTO(Long teamId, String serviceName, String ownerName, AppType appType, String topicSummary,
                              String imageUrl,
                              String serviceIntroFile, String featureRequirements, String preferredDeveloper) {
        public static TeamIdeaDTO from(Team team) {
            return new TeamIdeaDTO(
                    team.getId(),
                    team.getServiceName(),
                    team.getOwnerName() + " - " + team.getBatch().getDescription() + " PM",
                    team.getAppType(),
                    team.getTopicSummary(),
                    team.getImageUrl(),
                    team.getServiceIntroFile(),
                    team.getFeatureRequirements(),
                    team.getPreferredDeveloper());
        }
    }
}
