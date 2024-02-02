package ru.practicum.ewm.controller.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.debug("Добавление новой подборки.");
        CompilationDto savedCompilation = compilationService.saveCompilationByAdmin(compilationDto);
        log.debug("Подборка сохранена.");
        return savedCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") int compId) {
        log.debug("Удаление подборки: {}.", compId);
        compilationService.deleteCompilationByAdmin(compId);
        log.debug("Подборка удалена.");
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable int compId, @RequestBody UpdateCompilationRequest request) {
        log.debug("Пришел запрос на изменение подборок.");
        CompilationDto updatedCompilation = compilationService.updateCompilationByAdmin(compId, request);
        log.debug("Изменения сохранены.");
        return updatedCompilation;
    }
}
