package com.smusoak.restapi.response;

import com.smusoak.restapi.dto.ErrorCodeDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private boolean success;
    private Object data;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .success(false)
                        .data(ErrorCodeDto.builder()
                                .status(e.getHttpStatus().value())
                                .code(e.name())
                                .message(e.getMessage())
                                .build())
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e, String message){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .success(false)
                        .data(ErrorCodeDto.builder()
                                .status(e.getHttpStatus().value())
                                .code(e.name())
                                .message(message)
                                .build())
                        .build());
    }


}