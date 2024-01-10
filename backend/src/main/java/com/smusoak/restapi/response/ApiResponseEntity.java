package com.smusoak.restapi.response;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ApiResponseEntity {
    private boolean success;
    private Object data;

    public static ResponseEntity<ApiResponseEntity> toResponseEntity() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseEntity.builder()
                        .success(true)
                        .build());
    }
    public static ResponseEntity<ApiResponseEntity> toResponseEntity(Object data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseEntity.builder()
                        .success(true)
                        .data(data)
                        .build());
    }
}
