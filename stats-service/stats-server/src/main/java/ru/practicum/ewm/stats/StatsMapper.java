package ru.practicum.ewm.stats;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.model.Endpoint;
import ru.practicum.ewm.stats.model.Stat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StatsMapper {

    public static Endpoint toEndpoint(EndpointHit dto) {
        Endpoint endpoint = new Endpoint();
        endpoint.setIp(dto.getIp());
        endpoint.setUri(dto.getUri());
        endpoint.setApp(dto.getApp());
        endpoint.setDateTime(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return endpoint;
    }

    public static List<ViewStats> toViewStatList(List<Stat> stats) {
        return stats.stream()
                .map(StatsMapper::toViewStats)
                .collect(Collectors.toList());
    }

    public static ViewStats toViewStats(Stat stats) {
        return new ViewStats(stats.getApp(), stats.getUri(), stats.getHits());
    }
}
