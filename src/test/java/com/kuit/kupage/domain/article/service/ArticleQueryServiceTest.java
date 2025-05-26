package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.config.S3Config;
import com.kuit.kupage.common.file.PresignedUrlController;
import com.kuit.kupage.common.file.PresignedUrlService;
import com.kuit.kupage.common.file.S3Service;
import com.kuit.kupage.common.response.PagedResponse;
import com.kuit.kupage.domain.article.domain.*;
import com.kuit.kupage.domain.article.dto.ArticleDetailResponse;
import com.kuit.kupage.domain.article.dto.ArticleResponse;
import com.kuit.kupage.domain.article.dto.BlockResponse;
import com.kuit.kupage.domain.article.repository.ArticleRepository;
import com.kuit.kupage.domain.article.repository.ArticleTagRepository;
import com.kuit.kupage.domain.article.repository.BlockRepository;
import com.kuit.kupage.domain.article.repository.TagRepsitory;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.Grade;
import com.kuit.kupage.domain.detail.repository.DetailRepository;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.domain.oauth.service.DiscordOAuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

@SpringBootTest
@Transactional
class ArticleQueryServiceTest {

    @MockitoBean private S3Service s3Service;
    @MockitoBean private JwtTokenService jwtTokenService;
    @MockitoBean private S3Config s3Config;
    @MockitoBean private DiscordOAuthService discordOAuthService;
    @MockitoBean private PresignedUrlController presignedUrlController;
    @MockitoBean private PresignedUrlService presignedUrlService;

    @Autowired private ArticleQueryService service;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private ArticleTagRepository articleTagRepository;
    @Autowired private TagRepsitory tagRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private DetailRepository detailRepository;
    @Autowired private BlockRepository blockRepository;

    private Member savedMember;
    private Article savedArticle;

    @BeforeEach
    void setUp() {
        // 회원 및 Detail 생성
        Detail detail = Detail.of("user1", "202112322", "컴퓨터공학부", Grade.THIRD_YEAR,
                "test123", "test123", "01000000000", LocalDate.of(2001, 3, 20));
        detailRepository.save(detail);
        savedMember = memberRepository.save(
                Member.builder()
                        .name("user1")
                        .discordId("user1")
                        .discordLoginId("login1")
                        .profileImage("img.png")
                        .detail(detail)
                        .build()
        );

        // 태그 생성
        Tag tag1 = tagRepository.save(new Tag(null, "backend"));
        Tag tag2 = tagRepository.save(new Tag(null, "web"));
        List<Tag> tags = List.of(tag1, tag2);

        // 아티클 20개 생성 및 태그 & 블록 할당
        for (int i = 1; i <= 20; i++) {
            Article article = articleRepository.save(new Article(null, savedMember, "Article" + i));
            ArticleTag at = articleTagRepository.save(new ArticleTag(null, article, tags.get(i % tags.size())));
            // 첫 번째 아티클에만 블록 생성
            if (i == 1) {
                savedArticle = article;
                blockRepository.save(new Block(null, article, 1, BlockType.TEXT, "첫 번째 블록"));
                blockRepository.save(new Block(null, article, 2, BlockType.IMAGE, "image-url"));
            }
        }
    }

    @Test
    @DisplayName("전체 조회 시 20개 반환되고 페이징을 검증하고, 제목/닉네임도 올바른지 확인한다.")
    void whenNoTag_thenReturn20Articles() {
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
