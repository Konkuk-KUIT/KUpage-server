package com.kuit.kupage.domain.project;

import com.kuit.kupage.domain.comon.Batch;
import com.kuit.kupage.domain.project.entity.AppField;
import com.kuit.kupage.domain.project.entity.AppType;
import com.kuit.kupage.domain.project.entity.Project;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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
                "detail url",
                Batch.FIFTH,
                AppType.Android,
                List.of(AppField.마케팅, AppField.게임),
                List.of("IntelliJ", "React"),
                "detail_file_url",
                "service_links"
        );

        // when
        em.persist(project);
        em.flush();

        Project findProject = em.find(Project.class, 1L);
        System.out.println(findProject.getAppFields());
        System.out.println(findProject.getTechStacks());

        // then
    }
}
