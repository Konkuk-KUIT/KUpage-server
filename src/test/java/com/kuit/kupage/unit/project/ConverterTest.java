package com.kuit.kupage.unit.project;

import com.kuit.kupage.domain.common.Batch;
import com.kuit.kupage.domain.project.entity.AppField;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.project.entity.Project;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ConverterTest {

    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("Converter 학습 테스트")
//    @Commit
    void test() {
        // given
        Project project = new Project(
                null,
                "프로젝트1",
                "summary",
                "detail url",
                Batch.FIFTH,
                AppType.Android,
                List.of(AppField.마케팅, AppField.게임),
                List.of("IntelliJ", "React"),
                "mainImagePath",
                "description",
                "detail_file_url",
                "service_links"
        );

        // when
        em.persist(project);
        em.flush();
        em.clear();

        Project findProject = em.find(Project.class, 1L);
        assertThat(findProject.getAppFields().size()).isEqualTo(2);
        assertThat(findProject.getAppFields().get(0)).isEqualTo(AppField.마케팅);
        assertThat(findProject.getAppFields().get(1)).isEqualTo(AppField.게임);

        assertThat(findProject.getTechStacks().size()).isEqualTo(2);
        assertThat(findProject.getTechStacks().get(0)).isEqualTo("IntelliJ");
        assertThat(findProject.getTechStacks().get(1)).isEqualTo("React");

        // then
    }
}
