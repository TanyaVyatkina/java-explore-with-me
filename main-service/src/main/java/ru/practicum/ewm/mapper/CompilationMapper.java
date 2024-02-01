package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
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
            Set<EventShortDto> events = compilation.getEvents()
                    .stream()
                    .map(CompilationMapper::toEventShortDto)
                    .collect(Collectors.toSet());
            dto.setEvents(events);
        }
        return dto;
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Compilation toEntity(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.isPinned());
        compilation.setTitle(dto.getTitle());
        if (events != null) {
            compilation.setEvents(new HashSet<>(events));
        }
        return compilation;
    }

    private static EventShortDto toEventShortDto(Event event) {
        EventShortDto shortDto = new EventShortDto(event.getId(), event.getAnnotation(),
                event.getEventDate(), event.isPaid(), event.getTitle());
        shortDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        shortDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        return shortDto;
    }
}
