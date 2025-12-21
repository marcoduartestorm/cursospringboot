package com.apirestspringboot.apirest.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
