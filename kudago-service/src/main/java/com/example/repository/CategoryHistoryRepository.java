package com.example.repository;

import com.example.entity.history.CategoryHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {

  List<CategoryHistory> findByCategoryIdOrderByTimestampDesc(Long categoryId);
}
