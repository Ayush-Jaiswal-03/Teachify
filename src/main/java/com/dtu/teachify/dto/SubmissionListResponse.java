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
public class SubmissionListResponse {
    List<SubmissionDto> submitted;
    List<MemberDto> notSubmitted;
}
