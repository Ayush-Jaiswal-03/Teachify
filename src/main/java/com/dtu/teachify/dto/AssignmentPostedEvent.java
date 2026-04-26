package com.dtu.teachify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentPostedEvent {
    private String type;
    private Long classroomId;
    private String classroomName;
    private Long assignmentId;
    private String postedBy;
}
