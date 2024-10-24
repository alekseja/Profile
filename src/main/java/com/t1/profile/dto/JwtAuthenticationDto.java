package com.t1.profile.dto;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
