package com.kuit.kupage.domain.article;

import java.util.List;

public record UploadArticleRequest(
        String title,
        List<String> tags,
        List<UploadBlockRequest> blocks
) {
}
