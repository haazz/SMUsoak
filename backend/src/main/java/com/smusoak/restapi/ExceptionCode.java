package com.smusoak.restapi;
import lombok.Getter;

public enum ExceptionCode {
    USER_MAIL_DUPLICATE(400, "이미 등록된 mail입니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}