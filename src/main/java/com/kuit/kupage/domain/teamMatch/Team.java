package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.memberTeam.MemberTeam;
import com.kuit.kupage.domain.project.entity.AppType;
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

    private String topicSummary;

    @Lob
    private String mvpFeatures;

    @Lob
    private String expectedRequirements;

    @Column(length = 500)
    private String thumbnailUrl;

    @Lob
    private String pmGreeting;

    @Lob
    private String additionalAnswer1;

    @Lob
    private String additionalAnswer2;

    private Long ownerId; // memberId

    private String ownerName; // memberName

    @Enumerated(EnumType.STRING)
    private AppType appType;

    @Enumerated(EnumType.STRING)
    private Batch batch;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTeam> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamApplicant> teamApplicants = new ArrayList<>();



}
