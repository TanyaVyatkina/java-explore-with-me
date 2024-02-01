package ru.practicum.ewm.service.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoriesService {
    List<CategoryDto> getCategories(PageRequest page);

    CategoryDto getCategory(int catId);
}
