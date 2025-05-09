package com.kuit.kupage.domain.article.service;

import com.kuit.kupage.domain.article.domain.Tag;
import com.kuit.kupage.domain.article.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> findTags(List<String> names) {
        List<Tag> tags = tagRepository.findTagsByNameIn(names);
        return tags;
    }
}
