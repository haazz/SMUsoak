package com.smusoak.restapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
	private String mail;
	private String password;
}
