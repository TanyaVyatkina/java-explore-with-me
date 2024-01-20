package ru.practicum.ewm.stats;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void save(@RequestBody @Valid EndpointHit hit) {
        log.debug("Сохранение информации о том, что на uri = {} был отправлен запрос пользователем", hit.getUri());
        statsService.save(hit);
        log.debug("Информация сохранена.");
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam @NonNull String start, @RequestParam @NonNull String end,
                               @RequestParam(required = false) String[] uris,
                               @RequestParam(defaultValue = "false") boolean unique) {
        log.debug("Пришел запрос на получение статистики по параметрам: {}, {}, {}, {}.", start, end, uris, unique);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ViewStats> stats = statsService.get(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter), uris, unique);
        log.debug("Найдена статистика: {}.", stats);
        return stats;
    }
}
