package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.service.admin.AdminCategorySevice;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategorySevice adminCategorySevice;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.debug("Добавление новой категории.");
        CategoryDto savedCategory = adminCategorySevice.saveCategory(categoryDto);
        log.debug("Категория сохранена.");
        return savedCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") int catId) {
        log.debug("Удаление категории: {}.", catId);
        adminCategorySevice.deleteCategory(catId);
        log.debug("Категория удалена.");
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable int catId, @RequestBody @Valid NewCategoryDto categoryDto) {
        log.debug("Изменение категории: {}.", catId);
        CategoryDto updatedCategory = adminCategorySevice.updateCategory(catId, categoryDto);
        log.debug("Категория изменена.");
        return updatedCategory;
    }
}
