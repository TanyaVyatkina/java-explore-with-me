package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> searchCompilations(boolean pinned, PageRequest page) {
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);
        return CompilationMapper.toDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка c id = " + compId + " не найдена или недоступна."));
        return CompilationMapper.toDto(compilation);
    }
}
