package com.undina.parser_func.controller;

import com.undina.parser_func.service.ParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.ParserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/parser")
public class ParserController {
    private final ParserService parserService;

    @PostMapping()
    public ResponseEntity<Integer> getCalculation(@RequestBody ParserModel parserModel) {
        log.info("getCalculation  {}", parserModel);
        Integer result = parserService.getCalculation(parserModel);
        log.info("getCalculation result {}", result);
        return ResponseEntity.ok()
                .body(result);
    }
}
