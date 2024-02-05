package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.validator.Annotation;
import ru.practicum.ewm.validator.Description;
import ru.practicum.ewm.validator.Title;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventUserRequest {
    @Annotation
    private String annotation;
    private Integer category;
    @Description
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserStateAction stateAction;
    @Title
    private String title;
}
