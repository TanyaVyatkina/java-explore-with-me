package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private LocalDateTime updated;
}
