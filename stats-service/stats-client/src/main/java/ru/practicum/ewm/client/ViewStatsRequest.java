package ru.practicum.ewm.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ViewStatsRequest {
    LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    Boolean unique;
}
