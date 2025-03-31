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
    private String studentNumber;
    private String departName;
    private String grade;
    private String githubId;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;

}
