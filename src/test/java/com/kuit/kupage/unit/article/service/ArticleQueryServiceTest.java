package com.kuit.kupage.unit.article.service;

import com.kuit.kupage.common.response.PagedResponse;
import com.kuit.kupage.domain.article.domain.*;
import com.kuit.kupage.domain.article.dto.ArticleDetailResponse;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.BlockResponse;
import com.kuit.kupage.domain.article.repository.ArticleRepository;
import com.kuit.kupage.domain.article.repository.ArticleTagRepository;
import com.kuit.kupage.domain.article.repository.BlockRepository;
import com.kuit.kupage.domain.article.service.ArticleQueryService;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.Grade;
import com.kuit.kupage.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

@ExtendWith(MockitoExtension.class)
class ArticleQueryServiceTest {

    @InjectMocks
    private ArticleQueryService service;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ArticleTagRepository articleTagRepository;

    private Member savedMember;
    private Article savedArticle;
    private List<Article> allArticles;
    private List<Article> backendArticles;
    private List<Block> savedArticleBlocks;

    @BeforeEach
    void setUp() {
        // 회원 및 Detail 생성
        Detail detail = Detail.of("user1", "202112322", "컴퓨터공학부", Grade.THIRD_YEAR,
                "test123", "test123", "01000000000", LocalDate.of(2001, 3, 20));
        savedMember = Member.builder()
                .name("user1")
                .discordId("user1")
                .discordLoginId("login1")
                .profileImage("img.png")
                .detail(detail)
                .build();

        // 태그 생성
        Tag tagBackend = new Tag(1L, "backend");
        Tag tagWeb = new Tag(2L, "web");
        List<Tag> tags = List.of(tagBackend, tagWeb);

        allArticles = new ArrayList<>();
        backendArticles = new ArrayList<>();
        savedArticleBlocks = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            Article article = new Article((long) i, savedMember, "Article" + i, "");
            Tag tag = tags.get(i % tags.size());
            // Simulate ArticleTag association if needed
            if ("backend".equals(tag.getName())) {
                backendArticles.add(article);
            }
            allArticles.add(article);
            // 첫 번째 아티클에만 블록 생성
            if (i == 1) {
                savedArticle = article;
                savedArticleBlocks.add(new Block(1L, article, 1, BlockType.TEXT, "첫 번째 블록"));
                savedArticleBlocks.add(new Block(2L, article, 2, BlockType.IMAGE, "image-url"));
            }
        }

    }

    @Test
    @DisplayName("전체 조회 시 20개 반환되고 페이징을 검증하고, 제목/닉네임도 올바른지 확인한다.")
    void whenNoTag_thenReturn20Articles() {
        Mockito.when(articleRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + pageable.getPageSize(), allArticles.size());
                    List<Article> slice = allArticles.subList(start, end);
                    return new PageImpl<>(slice, pageable, allArticles.size());
                });

        PagedResponse<ArticleResponse> resp = service.listArticles(0, null);

        // 페이징 검증
        assertThat(resp.content()).hasSize(16);
        assertThat(resp.totalElements()).isEqualTo(20);
        assertThat(resp.totalPages()).isEqualTo(2);
        assertThat(resp.remainingPages()).isEqualTo(1);

        // 제목 검증
        assertThat(resp.content())
                .extracting(ArticleResponse::title)
                .allMatch(title -> title.startsWith("Article"));

        // 작성자 닉네임 검증
        assertThat(resp.content())
                .extracting(ArticleResponse::authorName)
                .allMatch(nick -> nick.equals(savedMember.getName()));
    }

    @Test
    @DisplayName("태그 backend 조회 시 10개 반환되고, 제목/닉네임도 검증한다.")
    void whenTagBackend_thenReturn10Articles() {
        Mockito.when(articleTagRepository.findByTag_Name(ArgumentMatchers.eq("backend"), ArgumentMatchers.any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    int start = (int) pageable.getOffset();
                    int end = Math.min(start + pageable.getPageSize(), backendArticles.size());
                    List<Article> slice = backendArticles.subList(start, end);

                    List<ArticleTag> articleTags = new ArrayList<>();
                    for (Article article : slice) {
                        ArticleTag articleTag = Mockito.mock(ArticleTag.class);
                        Mockito.when(articleTag.getArticle()).thenReturn(article);
                        articleTags.add(articleTag);
                    }

                    return new PageImpl<>(articleTags, pageable, backendArticles.size());
                });

        PagedResponse<ArticleResponse> resp = service.listArticles(0, "backend");

        assertThat(resp.content()).hasSize(10);
        assertThat(resp.totalElements()).isEqualTo(10);
        assertThat(resp.totalPages()).isEqualTo(1);

        assertThat(resp.content())
                .extracting(ArticleResponse::title)
                .allMatch(title -> title.startsWith("Article"));

        assertThat(resp.content())
                .extracting(ArticleResponse::authorName)
                .allMatch(nick -> nick.equals(savedMember.getName()));
    }

    @Test
    @DisplayName("게시글 아이디로 정상적인 게시글의 정보를 가져온다.")
    void detailById_success() {
        Mockito.when(articleRepository.findById(savedArticle.getId()))
                .thenReturn(Optional.of(savedArticle));

        Mockito.when(blockRepository.findAllByArticle_Id(savedArticle.getId()))
                .thenReturn(savedArticleBlocks);

        ArticleDetailResponse response = service.detailById(savedArticle.getId());

        assertThat(response.id()).isEqualTo(savedArticle.getId());
        assertThat(response.authorName()).isEqualTo(savedMember.getName());
        assertThat(response.title()).isEqualTo(savedArticle.getTitle());

        assertThat(response.content())
                .extracting(BlockResponse::position, BlockResponse::type, BlockResponse::properties)
                .containsExactly(
                        tuple(1, BlockType.TEXT, "첫 번째 블록"),
                        tuple(2, BlockType.IMAGE, "image-url")
                );
    }
}
