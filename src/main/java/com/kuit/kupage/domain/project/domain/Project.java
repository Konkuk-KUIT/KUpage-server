package com.kuit.kupage.domain.project.domain;

import com.kuit.kupage.common.type.BaseEntity;
import com.kuit.kupage.domain.project.domain.converter.AppFieldListStringConverter;
import com.kuit.kupage.domain.project.domain.converter.StringListStringConverter;
import com.kuit.kupage.domain.common.Batch;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    private String summary;

    @Column(length = 1024)
    private String detail_url;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Batch batch;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppType appType;

    @Convert(converter = AppFieldListStringConverter.class)
    private List<AppField> appFields;

    @Convert(converter = StringListStringConverter.class)
    private List<String> techStacks;

    private String mainImagePath;

    // notion url
    private String description;

    @Column(length = 1024)
    private String detail_file_url;

    private String service_links;

}
