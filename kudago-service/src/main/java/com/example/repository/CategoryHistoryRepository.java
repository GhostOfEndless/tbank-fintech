package com.example.repository;

import com.example.entity.history.CategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {

    List<CategoryHistory> findByCategoryIdOrderByTimestampDesc(Long categoryId);
}
