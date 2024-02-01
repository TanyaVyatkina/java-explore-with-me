package ru.practicum.ewm.service.admin;

import lombok.RequiredArgsConstructor;
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
public class AdminCompilationsServiceImpl implements AdminCompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto compilationDto) {
        List<Event> events = null;
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toEntity(compilationDto, events);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilation);
    }

    @Override
    public void deleteCompilation(int compId) {
        Compilation compilation = checkExist(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest request) {
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
