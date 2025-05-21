package com.kuit.kupage.domain.project.entity;

import com.kuit.kupage.domain.project.entity.converter.AppFieldListStringConverter;
import com.kuit.kupage.domain.project.entity.converter.StringListStringConverter;
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
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    private String detail_url;

    private Batch batch;

    private AppType appType;

    @Convert(converter = AppFieldListStringConverter.class)
    private List<AppField> appFields;

    @Convert(converter = StringListStringConverter.class)
    private List<String> techStacks;

    private String detail_file_url;

    private String service_links;

}
