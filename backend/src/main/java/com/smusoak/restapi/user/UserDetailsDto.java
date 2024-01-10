package com.smusoak.restapi.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDetailsDto {
    private String mail;
    private Integer age;
    private char gender;
    private String major;
}
