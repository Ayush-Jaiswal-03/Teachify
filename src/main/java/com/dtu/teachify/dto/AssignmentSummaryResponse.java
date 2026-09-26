package com.dtu.teachify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignmentSummaryResponse {
    private int studentCount;
    private List<AssignmentSummaryDto> assignmentSummaryList;
}
