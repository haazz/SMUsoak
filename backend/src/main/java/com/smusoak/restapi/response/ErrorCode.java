package com.smusoak.restapi.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_MAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 등록된 mail입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 mail입니다."),
    WRONG_MAIL_OR_PASSWORD(HttpStatus.NOT_FOUND, "mail이나 password를 확인해주세요."),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "알고리즘을 사용할 수 없습니다."),
    REDIS_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "Redis에 데이터가 존재하지 않습니다."),
    JWT_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.FORBIDDEN, "잘못된 JWT 토큰입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
