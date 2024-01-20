package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.stats.model.Endpoint;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void save(EndpointHit endpointHit) {
        Endpoint endpoint = StatsMapper.toEndpoint(endpointHit);
        statsRepository.save(endpoint);
    }

    @Override
    public List<ViewStats> get(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        if (unique && uris != null) {
            return StatsMapper.toViewStatList(statsRepository.findUniqueStatWithGivenUris(start, end, uris));
        }
        if (!unique && uris != null) {
            return StatsMapper.toViewStatList(statsRepository.findNotUniqueStatWithGivenUris(start, end, uris));
        }
        if (unique && uris == null) {
            return StatsMapper.toViewStatList(statsRepository.findUniqueStat(start, end));
        }
        return StatsMapper.toViewStatList(statsRepository.findNotUniqueStat(start, end));
    }
}
