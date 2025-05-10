package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.domain.article.UploadBlockRequest;
import com.kuit.kupage.domain.article.domain.Article;
import com.kuit.kupage.domain.article.domain.Block;
import com.kuit.kupage.domain.article.repository.BlockJdbcRepository;
import com.kuit.kupage.domain.article.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final BlockJdbcRepository blockJdbcRepository;

    @Transactional
    public List<Block> createBlocks(Article article, List<UploadBlockRequest> requests) {
        List<Block> blocks = requests.stream().map( r ->
                Block.of(article, r.position(), r.type(), r.properties())
        ).toList();

        blockJdbcRepository.saveAllBatch(blocks);
        List<Integer> positions = blocks.stream().map(Block::getPosition).toList();
        return blockRepository.findBlocksByArticleAndPositionIn(article, positions);
    }
}
