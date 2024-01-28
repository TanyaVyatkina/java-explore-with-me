package ru.practicum.ewm.controller.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.admin.AdminCompilationsService;
import ru.practicum.ewm.dto.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminCompilationsController {
    private final AdminCompilationsService adminCompilationsService;

    @PostMapping
    public CompilationDto saveCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.debug("Добавление новой подборки.");
        CompilationDto savedCompilation = adminCompilationsService.saveCompilation(compilationDto);
        log.debug("Подборка сохранена.");
        return savedCompilation;
    }

    @DeleteMapping("/{compId")
    public void deleteCompilation(@PathVariable("compId") int compId) {
        log.debug("Удаление подборки: {}.", compId);
        adminCompilationsService.deleteCompilation(compId);
        log.debug("Подборка удалена.");
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable int compId, @RequestBody UpdateCompilationRequest request) {
        log.debug("Пришел запрос на изменение подборок.");
        CompilationDto updatedCompilation = adminCompilationsService.updateCompilation(compId, request);
        log.debug("Изменения сохранены.");
        return updatedCompilation;
    }
}
