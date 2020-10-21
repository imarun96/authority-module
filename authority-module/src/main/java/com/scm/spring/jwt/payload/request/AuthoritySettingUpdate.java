package com.scm.spring.jwt.payload.request;

import java.util.Set;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthoritySettingUpdate {
	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	private Set<String> roles;
}