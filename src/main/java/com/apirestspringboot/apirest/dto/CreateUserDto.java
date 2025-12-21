package com.apirestspringboot.apirest.dto;

import java.util.List;

import com.apirestspringboot.apirest.model.Role;

public class CreateUserDto {
	
	private String username;
	
	private String password;
	
	private List<Role> roles;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
