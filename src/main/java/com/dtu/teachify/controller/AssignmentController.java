package com.dtu.teachify.controller;

import com.dtu.teachify.dto.AssignmentDto;
import com.dtu.teachify.dto.AssignmentRequest;
import com.dtu.teachify.dto.AssignmentSummaryDto;
import com.dtu.teachify.dto.AssignmentSummaryResponse;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.exception.ApiException;
import com.dtu.teachify.repository.ClassroomRepository;
import com.dtu.teachify.service.AssignmentService;
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
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ClassroomRepository classroomRepository;
    private final UserService userService;

    @PostMapping(value = "/create/{classroomId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Assignment createAssignment(@PathVariable Long classroomId, @RequestPart("data") AssignmentRequest assignmentInfo, @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) throws IOException {

        User currentUser = userService.authenticateAndGetUser();

//        ObjectMapper mapper = new ObjectMapper();
//        AssignmentDto assignmentInfo = mapper.readValue(data, AssignmentDto.class);

        Classroom classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Classroom not found"));

        Assignment assignment = assignmentService.createAssignment(classroom, currentUser, assignmentInfo, attachments);

        return assignment;
    }

    @GetMapping("/{assignmentId}")
    public AssignmentDto fetchAssignmentDetail(@PathVariable Long assignmentId){
        User user = userService.authenticateAndGetUser();
        return assignmentService.fetchAssignmentDetails(user.getId(), assignmentId);
    }

    @GetMapping("/{classroomId}/summary")
    public ResponseEntity<AssignmentSummaryResponse> fetchAssignmentsSummary(@PathVariable Long classroomId){
        AssignmentSummaryResponse assignmentSummaryResponse = assignmentService.fetchAssignmentSummary(classroomId);
        return ResponseEntity.ok(assignmentSummaryResponse);
    }

//    @GetMapping("/get-all/{classroomId}")
//    public List<Assignment> getAllAssignments(@PathVariable Long classroomId){
//        User currentUser = userService.getCurrentUser();
//
//        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Classroom not found"));
//
//        return assignmentService.getAllAssignments(classroom);
//    }


}
