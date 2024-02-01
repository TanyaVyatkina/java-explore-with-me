package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CompilationDto;

import java.util.List;

public interface CompilationsService {
    List<CompilationDto> searchCompilations(Boolean pinned, PageRequest page);
    CompilationDto getCompilation(int compId);
}
