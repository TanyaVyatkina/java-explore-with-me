package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    private List<Integer> events;
    private boolean pinned;
    @NotBlank
    @Max(50)
    @Min(1)
    private String title;
}
