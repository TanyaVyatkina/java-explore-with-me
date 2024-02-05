package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(PageRequest page);

    CategoryDto getCategory(int catId);

    CategoryDto saveCategoryByAdmin(NewCategoryDto categoryDto);

    void deleteCategoryByAdmin(int categoryId);

    CategoryDto updateCategoryByAdmin(int categoryId, NewCategoryDto categoryDto);
}
