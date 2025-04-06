package com.kuit.kupage.domain.detail;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Detail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    private String name;
    private Long studentNumber;
    private String departName;

    @Enumerated(EnumType.STRING)
    private Grade grade;
    private String githubId;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;

    public static Detail of(String name, Long studentNumber, String departName, Grade grade, String githubId, String email, String phoneNumber, LocalDate birthday) {
        Detail detail = new Detail();
        detail.name = name;
        detail.studentNumber = studentNumber;
        detail.departName = departName;
        detail.grade = grade;
        detail.githubId = githubId;
        detail.email = email;
        detail.phoneNumber = phoneNumber;
        detail.birthday = birthday;
        return detail;
    }

}
