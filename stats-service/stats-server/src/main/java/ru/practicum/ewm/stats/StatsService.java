package ru.practicum.ewm.stats;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void save(EndpointHit endpointHit);

    List<ViewStats> get(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
