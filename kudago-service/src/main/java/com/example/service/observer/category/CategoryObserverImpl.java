package com.example.service.observer.category;

import com.example.entity.Category;
import com.example.entity.CrudAction;
import com.example.entity.history.CategoryMemento;
import com.example.service.history.CategoryHistoryCaretaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryObserverImpl implements CategoryObserver {

  private final CategoryHistoryCaretaker historyCaretaker;

  @Override
  public void onLocationChanged(Category category, CrudAction action) {
    historyCaretaker.saveSnapshot(CategoryMemento.createSnapshot(category, action));
  }
}
