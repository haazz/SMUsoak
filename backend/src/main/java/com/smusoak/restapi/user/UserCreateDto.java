package com.smusoak.restapi.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
	private String studentid;
	private String password;
}
