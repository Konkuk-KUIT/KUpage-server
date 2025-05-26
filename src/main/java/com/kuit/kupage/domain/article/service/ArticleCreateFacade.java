package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.BlockType;
import com.kuit.kupage.domain.article.domain.Tag;
import com.kuit.kupage.domain.article.dto.UploadArticleRequest;
import com.kuit.kupage.domain.article.dto.UploadBlockRequest;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.service.MemberService;
import com.kuit.kupage.exception.ArticleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCreateFacade {

    private final MemberService memberService;
    private final TagService tagService;
    private final ArticleService articleService;
    private final BlockService blockService;
    private final ArticleTagService articleTagService;

    private static final int IMAGE_BLOCK_MAX_COUNT = 10;
    private static final int FILE_BLOCK_MAX_COUNT = 3;

    @Transactional
    public Article createArticle(UploadArticleRequest request, Long memberId) {
        validateBlockPosition(request.blocks());
        validateFileBlockCount(request.blocks());
        validateImageBlockCount(request.blocks());

        Member member = memberService.getMember(memberId);
        List<Tag> tags = tagService.findTags(request.tags());
        Article article = articleService.createArticle(member, request.title(), request.thumbnailImagePath());
        articleTagService.createArticleTags(article, tags);
        blockService.createBlocks(article, request.blocks());

        return article;
    }

    private void validateBlockPosition(List<UploadBlockRequest> blocks) {
        int setSize = blocks.stream()
                .map(UploadBlockRequest::position)
                .collect(Collectors.toSet())
                .size();
        if(blocks.size() != setSize) {
            throw new ArticleException(ResponseCode.INVALID_POSITIONS);
        }
    }

    private void validateFileBlockCount(List<UploadBlockRequest> blocks) {
        int fileCount = blocks.stream()
                .map(b -> b.type() == BlockType.FILE)
                .toList()
                .size();
        if(fileCount > FILE_BLOCK_MAX_COUNT)
            throw new ArticleException(ResponseCode.TOO_MANY_FILE);
    }

    private void validateImageBlockCount(List<UploadBlockRequest> blocks) {
        int imageCount = blocks.stream()
                .map(b -> b.type() == BlockType.IMAGE)
                .toList()
                .size();
        if(imageCount > IMAGE_BLOCK_MAX_COUNT)
            throw new ArticleException(ResponseCode.TOO_MANY_IMAGE);
    }
}
