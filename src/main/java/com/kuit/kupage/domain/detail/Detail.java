package com.kuit.kupage.domain.detail;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Detail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    private String name;
    private String studentNumber;
    private String departName;

    @Enumerated(EnumType.STRING)
    private Grade grade;
    private String githubId;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;

    public static Detail of(String name, String studentNumber, String departName, Grade grade, String githubId, String email, String phoneNumber, LocalDate birthday) {
        Detail detail = Detail.builder()
                .name(name)
                .studentNumber(studentNumber)
                .departName(departName)
                .grade(grade)
                .githubId(githubId)
                .email(email)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();

        return detail;
    }

}
