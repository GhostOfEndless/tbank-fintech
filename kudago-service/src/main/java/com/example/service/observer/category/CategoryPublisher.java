package com.example.service.observer.category;

import com.example.entity.Category;
import com.example.entity.CrudAction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryPublisher {

  private final List<CategoryObserver> observers;

  public void notifyObservers(Category category, CrudAction action) {
    observers.forEach(observer -> observer.onLocationChanged(category, action));
  }
}
