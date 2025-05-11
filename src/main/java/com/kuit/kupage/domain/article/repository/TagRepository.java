package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findTagsByNameIn(List<String> names);
}
