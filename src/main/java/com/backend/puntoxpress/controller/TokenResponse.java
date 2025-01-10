package com.backend.puntoxpress.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token")
        String acessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
