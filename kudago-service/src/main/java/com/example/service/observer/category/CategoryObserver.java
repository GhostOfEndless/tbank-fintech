package com.example.service.observer.category;

import com.example.entity.Category;
import com.example.entity.CrudAction;

public interface CategoryObserver {

    void onLocationChanged(Category category, CrudAction action);
}
