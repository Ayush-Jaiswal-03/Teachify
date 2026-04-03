package com.dtu.teachify.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;
}
