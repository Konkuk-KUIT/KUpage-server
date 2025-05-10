package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepsitory extends JpaRepository<Tag, Long> {

}
