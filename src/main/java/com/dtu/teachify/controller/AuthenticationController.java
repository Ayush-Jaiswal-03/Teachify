package com.dtu.teachify.controller;

import com.dtu.teachify.dto.LoginUserDto;
import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.response.ApiResponse;
import com.dtu.teachify.response.AuthResponse;
import com.dtu.teachify.service.AuthenticationService;
import com.dtu.teachify.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody UserDto input) {
        User registeredUser = authenticationService.signup(input);

        String jwtToken = jwtService.generateToken(registeredUser);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(registeredUser.getId());
        authResponse.setUsername(registeredUser.getUsername());
        authResponse.setEmail(registeredUser.getEmail());
        authResponse.setToken(jwtToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", authResponse));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody LoginUserDto input) {
        User authenticatedUser = authenticationService.authenticate(input);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(authenticatedUser.getId());
        authResponse.setUsername(authenticatedUser.getUsername());
        authResponse.setEmail(authenticatedUser.getEmail());
        authResponse.setToken(jwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Logged In successfully", authResponse));
    }
}