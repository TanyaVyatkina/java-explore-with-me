package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> searchCompilations(Boolean pinned, PageRequest page) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, page);
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }
        return CompilationMapper.toDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка c id = " + compId + " не найдена или недоступна."));
        return CompilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto saveCompilationByAdmin(NewCompilationDto compilationDto) {
        List<Event> events = null;
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toEntity(compilationDto, events);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilation);
    }

    @Override
    public void deleteCompilationByAdmin(int compId) {
        Compilation compilation = checkExist(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilationByAdmin(int compId, UpdateCompilationRequest request) {
        validateUpdateCompilationRequest(request);
        Compilation compilation = checkExist(compId);
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getEvents() == null) {
            compilation.setEvents(null);
        } else {
            List<Event> newEvents = eventRepository.findByIdIn(request.getEvents());
            compilation.setEvents(new HashSet<>(newEvents));
        }
        compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilation);
    }

    private Compilation checkExist(int compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена."));
    }

    private void validateUpdateCompilationRequest(UpdateCompilationRequest request) {
        String title = request.getTitle();
        if (title != null && (title.isBlank() || title.length() > 50)) {
            throw new ValidationException("Поле title должно быть в диапазоне от 1 до 50.");
        }
    }
}
