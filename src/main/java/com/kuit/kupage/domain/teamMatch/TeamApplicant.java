package com.kuit.kupage.domain.teamMatch;

import com.kuit.kupage.common.type.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamApplicant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_applicant_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 20)
    private String studentId;

    @Embedded
    private Part appliedPart;

    @Lob
    private String motivation;

    @Column(length = 500)
    private String portfolioUrl;

    @Lob
    private String additionalAnswer1;

    @Lob
    private String additionalAnswer2;
}
