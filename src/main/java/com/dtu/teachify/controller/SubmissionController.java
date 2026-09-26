package com.dtu.teachify.controller;

import com.dtu.teachify.dto.AssignmentSummaryDto;
import com.dtu.teachify.dto.SubmissionDto;
import com.dtu.teachify.dto.SubmissionListResponse;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.response.ApiResponse;
import com.dtu.teachify.service.SubmissionService;
import com.dtu.teachify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserService userService;

    @PostMapping(value = "/{assignmentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubmissionDto>> submitAssignment(@PathVariable Long assignmentId, @RequestParam("files") MultipartFile[] files) throws IOException {
        User user = userService.authenticateAndGetUser();

        SubmissionDto submission = submissionService.submitAssignment(user, assignmentId, files);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Assignment Submitted Successfully ...", submission));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> fetchAllSubmissions(@PathVariable Long assignmentId){
        User user = userService.authenticateAndGetUser();

        SubmissionListResponse submissionListResponse = submissionService.fetchAllSubmissionsForAssignment(assignmentId);

        return ResponseEntity.ok(submissionListResponse);

    }



}
