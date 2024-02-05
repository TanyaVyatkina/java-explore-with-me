package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на поиск категорий.");
        PageRequest page = PageRequest.of(from / size, size);
        List<CategoryDto> foundCategories = categoryService.getCategories(page);
        log.debug("Найдены категории", foundCategories);
        return foundCategories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable int catId) {
        log.debug("Пришел запрос на поиск категории id = {}.", catId);
        CategoryDto foundCategory = categoryService.getCategory(catId);
        log.debug("Найдена категория : {}", foundCategory);
        return foundCategory;
    }
}
