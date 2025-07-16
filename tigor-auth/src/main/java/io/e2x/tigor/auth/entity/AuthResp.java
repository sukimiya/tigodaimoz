package io.e2x.tigor.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResp {
    private String username;
    private String token;
    private String refreshToken;
    private Long expiration;
}
