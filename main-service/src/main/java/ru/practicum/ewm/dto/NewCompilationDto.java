package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    private List<Integer> events;
    private boolean pinned;
    @NotBlank
    @Size(max = 50, min = 1)
    private String title;
}
