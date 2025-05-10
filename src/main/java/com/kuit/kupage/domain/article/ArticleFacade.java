package com.kuit.kupage.domain.article;

import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.Tag;
import com.kuit.kupage.domain.article.service.ArticleService;
import com.kuit.kupage.domain.article.service.ArticleTagService;
import com.kuit.kupage.domain.article.service.BlockService;
import com.kuit.kupage.domain.article.service.TagService;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleFacade {

    private final MemberService memberService;
    private final TagService tagService;
    private final ArticleService articleService;
    private final BlockService blockService;
    private final ArticleTagService articleTagService;

    @Transactional
    public Article createArticle(UploadArticleRequest request, Long memberId) {

        Member member = memberService.getMember(memberId);
        List<Tag> tags = tagService.findTags(request.tags());
        Article article = articleService.createArticle(member, request.title());
        articleTagService.createArticleTags(article, tags);
        blockService.createBlocks(article, request.blocks());

        return article;
    }
}
