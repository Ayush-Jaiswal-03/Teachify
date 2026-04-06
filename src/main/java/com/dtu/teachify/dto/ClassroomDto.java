package com.dtu.teachify.dto;

import com.dtu.teachify.enums.Role;
import lombok.Builder;
import lombok.Data;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClassroomDto {
    private String name;
    private String description;
    private String joinCode;
    private Role role;
}
