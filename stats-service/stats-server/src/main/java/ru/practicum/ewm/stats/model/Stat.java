package ru.practicum.ewm.stats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stat {
    private String app;
    private String uri;
    private long hits;
}
