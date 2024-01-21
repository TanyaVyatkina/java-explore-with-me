package ru.practicum.ewm.stats;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.stats.model.Endpoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {

    public static Endpoint toEndpoint(EndpointHit dto) {
        Endpoint endpoint = new Endpoint();
        endpoint.setIp(dto.getIp());
        endpoint.setUri(dto.getUri());
        endpoint.setApp(dto.getApp());
        endpoint.setDateTime(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return endpoint;
    }
}
