package com.apirestspringboot.apirest.model;

import jakarta.validation.constraints.NotBlank;

public class UserEmailDto {

	@NotBlank
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
