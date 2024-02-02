package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> searchCompilations(Boolean pinned, PageRequest page);

    CompilationDto getCompilation(int compId);

    CompilationDto saveCompilationByAdmin(NewCompilationDto compilationDto);

    void deleteCompilationByAdmin(int compId);

    CompilationDto updateCompilationByAdmin(int compId, UpdateCompilationRequest request);
}
