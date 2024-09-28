package com.example.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class DataLoaderTest {

    @Mock
    CategoryService categoryService;

    @Mock
    LocationService locationService;

    @InjectMocks
    DataLoader dataLoader;

    @Test
    @Tag("onApplicationEvent")
    @DisplayName("Should initialize both location and category services")
    public void onApplicationEvent_loadData() {
        dataLoader.onApplicationEvent();

        verify(locationService).init();
        verify(categoryService).init();
        verifyNoMoreInteractions(locationService);
        verifyNoMoreInteractions(categoryService);
    }
}
