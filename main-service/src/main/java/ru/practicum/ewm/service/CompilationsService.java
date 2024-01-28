package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CompilationDto;

import java.util.List;

public interface CompilationsService {
    List<CompilationDto> searchCompilations(boolean pinned, PageRequest page);
    CompilationDto getCompilation(int compId);
}
