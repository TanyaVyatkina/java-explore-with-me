package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {
    private List<Integer> events;
    private boolean pinned;
    private String title;
}
