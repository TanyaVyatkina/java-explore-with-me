package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> searchCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.debug("Пришел запрос на поиск подборок.");
        PageRequest page = PageRequest.of(from / size, size);
        List<CompilationDto> foundCompilations = compilationService.searchCompilations(pinned, page);
        log.debug("Найдены подборки: {}.", foundCompilations);
        return foundCompilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable int compId) {
        log.debug("Пришел запрос на поиск подборки: {}.", compId);
        CompilationDto foundCompilation = compilationService.getCompilation(compId);
        log.debug("Найдена подборка: {}.", foundCompilation);
        return foundCompilation;
    }
}
