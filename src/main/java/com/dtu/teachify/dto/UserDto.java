package com.dtu.teachify.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;

}
