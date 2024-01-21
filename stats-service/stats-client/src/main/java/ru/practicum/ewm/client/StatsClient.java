package ru.practicum.ewm.client;

import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.util.List;

public interface StatsClient {
    void saveEndpoint(EndpointHit endpointHit);

    List<ViewStats> findStatistic(ViewStatsRequest request);
}
