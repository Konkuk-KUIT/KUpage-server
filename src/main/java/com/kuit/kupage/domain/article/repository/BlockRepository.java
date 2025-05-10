package com.kuit.kupage.domain.article.repository;

import com.kuit.kupage.domain.article.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
