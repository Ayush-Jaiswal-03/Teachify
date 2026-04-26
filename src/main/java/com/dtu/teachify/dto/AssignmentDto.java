package com.dtu.teachify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignmentDto {
    private Long id;
    private String title;
    private String instructions;
    private Double points;
    private LocalDateTime dueDate;
    private List<AttachmentDto> attachments;
}
