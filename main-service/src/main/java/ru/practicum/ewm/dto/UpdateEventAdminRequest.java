package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.validator.Annotation;
import ru.practicum.ewm.validator.Description;
import ru.practicum.ewm.validator.EventDate;
import ru.practicum.ewm.validator.Title;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventAdminRequest {
    @Annotation
    private String annotation;
    private Integer category;
    @Description
    private String description;
    @EventDate
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;
    @Title
    private String title;
}
