package com.example.service.history;

import com.example.entity.history.CategoryHistory;
import com.example.entity.history.CategoryMemento;
import com.example.repository.CategoryHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryHistoryCaretaker {

  private final CategoryHistoryRepository historyRepository;

  public void saveSnapshot(CategoryMemento memento) {
    var history = CategoryHistory.builder()
        .categoryId(memento.id())
        .slug(memento.slug())
        .name(memento.name())
        .timestamp(memento.timestamp())
        .action(memento.action())
        .build();

    historyRepository.save(history);
  }

  public List<CategoryMemento> getHistory(Long categoryId) {
    return historyRepository.findByCategoryIdOrderByTimestampDesc(categoryId)
        .stream()
        .map(history -> new CategoryMemento(
            history.getCategoryId(),
            history.getSlug(),
            history.getName(),
            history.getTimestamp(),
            history.getAction()
        ))
        .toList();
  }
}
