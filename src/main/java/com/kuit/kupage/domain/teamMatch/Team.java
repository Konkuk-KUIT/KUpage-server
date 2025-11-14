package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.memberTeam.MemberTeam;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.teamMatch.dto.IdeaRegisterRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private Long ownerId; // memberId

    private String ownerName; // memberName

    @Enumerated(EnumType.STRING)
    private Batch batch;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTeam> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamApplicant> teamApplicants = new ArrayList<>();


    public Team(Long ownerId, String ownerName, Batch currentBatch, IdeaRegisterRequest request) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.batch = currentBatch;
        this.serviceName = request.serviceName();
        this.appType = request.appType();
        this.topicSummary = request.topicSummary();
        this.imageUrl = request.imageUrl();
        this.serviceIntroFile = request.serviceIntroFile();
        this.featureRequirements = request.featureRequirements();
        this.preferredDeveloper = request.preferredDeveloper();
    }
}
