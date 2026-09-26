package com.dtu.teachify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionAttachmentDto {
    private Long id;
    private Long submissionId;
    private String fileName;
    private String fileUrl;
}
