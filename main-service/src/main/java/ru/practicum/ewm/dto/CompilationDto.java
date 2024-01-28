package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CompilationDto {
    private Integer id;
    private boolean pinned;
    private String title;
    private Set<EventShortDto> events;

    public CompilationDto(Integer id, boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }
}
