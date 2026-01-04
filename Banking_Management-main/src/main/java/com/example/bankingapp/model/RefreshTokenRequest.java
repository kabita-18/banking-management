package com.example.bankingapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class RefreshTokenRequest {
	private String refreshToken;
	@JsonCreator
    public RefreshTokenRequest(
        @JsonProperty("refreshToken") String refreshToken
    ) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
