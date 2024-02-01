package ru.practicum.ewm.service.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoriesService {
    List<CategoryDto> getCategories(PageRequest page);
    CategoryDto getCategory(int catId);
}
