package com.dtu.teachify.dto;

import com.dtu.teachify.enums.SubmissionStatus;
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
public class SubmissionDto {

    private Long id;
    private Long userId;
    private String username;
    private SubmissionStatus status;
    private Double points;
    private LocalDateTime submittedAt;
    private String teacherComment;
    private List<SubmissionAttachmentDto> attachments;

    public SubmissionDto(Long id, Long userId, String username, SubmissionStatus status, Double points, LocalDateTime submittedAt, String teacherComment ){
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.points = points;
        this.submittedAt = submittedAt;
        this.teacherComment = teacherComment;
    }

}
