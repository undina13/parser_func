package com.undina.parser_func.controller;

import com.undina.parser_func.exception.CalculationException;
import com.undina.parser_func.exception.ErrorResponse;
import com.undina.parser_func.exception.FunctionException;
import com.undina.parser_func.exception.VariableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FunctionException.class)
    public ResponseEntity<ErrorResponse> functionException(final FunctionException e) {
        log.error("functionException " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(VariableException.class)
    public ResponseEntity<ErrorResponse> variableException(final VariableException e) {
        log.error("variableException " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> calculationException(final CalculationException e) {
        log.error("calculationException " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }
}
