package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(PageRequest page) {
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(int catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + "не найдена."));
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto saveCategoryByAdmin(NewCategoryDto newCat) {
        Category category = categoryRepository.save(CategoryMapper.toEntity(newCat));
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryByAdmin(int categoryId) {
        checkExistence(categoryId);
        checkEventsExist(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryByAdmin(int categoryId, NewCategoryDto categoryDto) {
        Category category = checkExistence(categoryId);
        category.setName(categoryDto.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.toDto(category);
    }

    private Category checkExistence(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена или недоступна."));
    }

    private void checkEventsExist(int catId) {
        if (eventRepository.countAllByCategory_Id(catId) > 0) {
            throw new ConflictException("Существуют события, связанные с категорией.");
        }
    }
}
