package com.dtu.teachify.response;

import lombok.Data;

@Data
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String token;
}
