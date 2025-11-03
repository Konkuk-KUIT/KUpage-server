package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppType appType;

    @Column(length = 500)
    private String topicSummary;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String serviceIntroFile;

    @Column(length = 1000)
    private String featureRequirements;

    @Column(length = 1000)
    private String preferredDeveloper;

    public Team(IdeaRegisterRequest request) {
        this.serviceName = request.serviceName();
        this.appType = request.appType();
        this.topicSummary = request.topicSummary();
        this.imageUrl = request.imageUrl();
        this.serviceIntroFile = request.serviceIntroFile();
        this.featureRequirements = request.featureRequirements();
        this.preferredDeveloper = request.preferredDeveloper();
    }
}
