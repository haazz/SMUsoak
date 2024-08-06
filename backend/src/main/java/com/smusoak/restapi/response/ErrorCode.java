package com.smusoak.restapi.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "request 필수 항목을 확인하세요."),
    USER_MAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 등록된 mail입니다."),
    USER_NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 등록된 nickname입니다."),
    WRONG_MAIL_ADDRESS(HttpStatus.BAD_REQUEST, "잘못된 메일 주소입니다."),
    WRONG_AUTH_CODE(HttpStatus.BAD_REQUEST, "잘못된 인증코드입니다. 인증을 재시도 해주세요!"),
    WRONG_PASSWORD_RULE(HttpStatus.BAD_REQUEST, "패스워드 규칙을 다시 확인해주세요."),
    MIN_USER_CREATE_CHATROOM(HttpStatus.BAD_REQUEST, "채팅룸 생성을 위해서는 최소 2명의 유저가 있어야 합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾지 못 하였습니다."),
    REDIS_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "Redis 데이터가 존재하지 않거나 만료되었습니다."),
    S3_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "S3 데이터가 존재하지 않거나 만료되었습니다."),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "알고리즘을 사용할 수 없습니다."),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
