package com.example.bootstrap.command.impl;

import com.example.bootstrap.command.DataInitializationCommand;
import com.example.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryInitializationCommand implements DataInitializationCommand {

    private final CategoryService categoryService;

    @Override
    public void execute() {
        log.info("Executing category initialization command");
        categoryService.init();
    }
}
