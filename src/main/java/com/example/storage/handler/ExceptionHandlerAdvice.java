package com.example.storage.handler;

import com.example.storage.exception.ErrorInputDataException;
import com.example.storage.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.storage.handler.IdGenerator.generateId;

@RestControllerAdvice
public class ExceptionHandlerAdvice {


    //Ошибка введенных данных
    @ExceptionHandler(ErrorInputDataException.class)
    public ResponseEntity<ErrorResponse> invalidParameter(ErrorInputDataException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(generateId(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(generateId(), e.getMessage()));
    }

}
