package ru.practicum.ewm.service.admin;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

public interface AdminCategorySevice {
    CategoryDto saveCategory(NewCategoryDto categoryDto);
    void deleteCategory(int categoryId);
    CategoryDto updateCategory(int categoryId, NewCategoryDto categoryDto);
}
