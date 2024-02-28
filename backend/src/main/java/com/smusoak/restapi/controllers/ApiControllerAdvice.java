package com.smusoak.restapi.controllers;

import com.smusoak.restapi.response.ErrorCode;
import com.smusoak.restapi.response.ErrorResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseEntity> handleValidationExceptions(MethodArgumentNotValidException ex){
        String errorMessage = "";
        boolean commas = false;
        for(ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (commas) {
                errorMessage = errorMessage + ", " + ((FieldError) error).getField();
            }
            else {
                commas = true;
                errorMessage = errorMessage + ((FieldError) error).getField();
            }
        }
        return ErrorResponseEntity.toResponseEntity(ErrorCode.BAD_REQUEST, "필수 항목: " + errorMessage);
    }
}
