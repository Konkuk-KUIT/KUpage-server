package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
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
}
