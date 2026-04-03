package com.dtu.teachify.dto;

import lombok.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long id;
    private String userName;
    private String email;
    private String password;
    private Instant createdAt;

}
