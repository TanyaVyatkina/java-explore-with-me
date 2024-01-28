package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto(compilation.getId(), compilation.isPinned(), compilation.getTitle());
        if (compilation.getEvents() != null) {
            dto.setEvents(new HashSet<>(EventMapper.toShortDtoList(compilation.getEvents())));
        }
        return dto;
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Compilation toEntity(NewCompilationDto dto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.isPinned());
        compilation.setTitle(dto.getTitle());
        compilation.setEvents(events);
        return compilation;
    }
}
