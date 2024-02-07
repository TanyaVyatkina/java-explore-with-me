package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class NewCommentDto {
    @NotBlank
    @Size(max = 1000)
    private String text;
}
