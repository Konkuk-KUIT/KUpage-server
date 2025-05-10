package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.BlockType;
import com.kuit.kupage.domain.article.domain.Tag;
import com.kuit.kupage.domain.article.dto.UploadArticleRequest;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.exception.ArticleException;
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

    private static final int IMAGE_BLOCK_MAX_COUNT = 10;
    private static final int FILE_BLOCK_MAX_COUNT = 3;

    @Transactional
    public Article createArticle(UploadArticleRequest request, Long memberId) {
        validateImageFileBlockCount(request);

        Member member = memberService.getMember(memberId);
        List<Tag> tags = tagService.findTags(request.tags());
        Article article = articleService.createArticle(member, request.title());
        articleTagService.createArticleTags(article, tags);
        blockService.createBlocks(article, request.blocks());

        return article;
    }

    private static void validateImageFileBlockCount(UploadArticleRequest request) {
        int imageCount = request.blocks().stream()
                .map(b -> b.type() == BlockType.IMAGE)
                .toList()
                .size();
        if(imageCount > IMAGE_BLOCK_MAX_COUNT)
            throw new ArticleException(ResponseCode.TOO_MANY_IMAGE);

        int fileCount = request.blocks().stream()
                .map(b -> b.type() == BlockType.FILE)
                .toList()
                .size();
        if(fileCount > FILE_BLOCK_MAX_COUNT)
            throw new ArticleException(ResponseCode.TOO_MANY_FILE);
    }
}
