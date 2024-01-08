package com.smusoak.restapi.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDto {
    private String mail;
    private Integer age;
    private char gender;
    private String major;
}
