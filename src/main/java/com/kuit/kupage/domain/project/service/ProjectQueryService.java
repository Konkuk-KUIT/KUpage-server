package com.kuit.kupage.domain.project.service;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.project.dto.*;
import com.kuit.kupage.domain.project.domain.Category;
import com.kuit.kupage.domain.project.domain.MemberProject;
import com.kuit.kupage.domain.project.domain.Project;
import com.kuit.kupage.domain.project.domain.Review;
import com.kuit.kupage.domain.project.repository.CategoryRepository;
import com.kuit.kupage.domain.project.repository.MemberProjectRepository;
import com.kuit.kupage.domain.project.repository.ProjectRepository;
import com.kuit.kupage.domain.project.repository.ReviewRepository;
import com.kuit.kupage.exception.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequestMapping("/projects")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService {

    private final MemberProjectRepository memberProjectRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/{projectId}")
    public ProjectResponse getProject(@PathVariable("projectId") Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ResponseCode.NONE_PROJECT));

        List<Category> categories = categoryRepository.findAllByProjectId(projectId);

        String[] categoryDtos = categories.stream().map(Category::getName).toArray(String[]::new);

        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectIdWithMember(projectId);

        // 프로젝트 진행한 멤버
        // 영속화 되어있는 member에 접근하므로 쿼리 발생 x
        List<Member> members = memberProjects.stream()
                .map(MemberProject::getMember)
                .toList();

        // 프로젝트 리뷰
        List<Review> reviews = reviewRepository.findByProjectId(project.getId());

        ProjectInfo projectInfo = ProjectInfo.builder()
                .batch(project.getBatch())
                .appType(project.getAppType())
                .categories(categoryDtos)
                .projectName(project.getSummary() + ", " + project.getName())
                .members(members.stream().map(Member::getName).toArray(String[]::new))
                .tools(project.getTechStacks().toArray(String[]::new)).build();

        ProjectDetail projectDetail = ProjectDetail.builder()
                .mainImagePath(project.getMainImagePath())
                .detail_file_url(project.getDetail_file_url())
                .projectDescription(project.getDescription()).build();

        ReviewContent[] reviewContents = reviews.stream().map(review ->
                ReviewContent.builder()
                        .nameInfo(review.getMemberDesc())
                        .content(review.getReview()).build()
        ).toArray(ReviewContent[]::new);

        return ProjectResponse.builder()
                .projectId(projectId)
                .projectInfo(projectInfo)
                .projectDetail(projectDetail)
                .reviewDetails(new ReviewDetails(reviewContents)).build();
    }
}
