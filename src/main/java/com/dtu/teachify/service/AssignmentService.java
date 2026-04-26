package com.dtu.teachify.service;

import com.dtu.teachify.dto.AssignmentDto;
import com.dtu.teachify.dto.AssignmentRequest;
import com.dtu.teachify.dto.AttachmentDto;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.Member;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.enums.Role;
import com.dtu.teachify.repository.AssignmentRepository;
import com.dtu.teachify.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final MemberRepository memberRepository;
    private final AssignmentRepository assignmentRepository;
    private final AttachmentService attachmentService;
    private final SNSService snsService;

    public Assignment createAssignment(Classroom classroom, User user, AssignmentRequest assignmentInfo, MultipartFile[] attachments) throws IOException {

        Member member = memberRepository.findByClassroomIdAndUserId(classroom.getId(), user.getId());

        if(member.getRole() != Role.TEACHER){
            throw new RuntimeException("Only teacher can create assignment ...");
        }

        Assignment assignment = Assignment.builder()
                .title(assignmentInfo.getTitle())
                .instructions(assignmentInfo.getInstructions())
                .points(assignmentInfo.getPoints())
                .dueDate(LocalDateTime.parse(assignmentInfo.getDueDate()))
                .classroom(classroom)
                .createdBy(user.getId())
                .build();

        Assignment createdAssignment = assignmentRepository.save(assignment);

        attachmentService.uploadFiles(assignment, attachments);

        snsService.publishAssignmentPosted(classroom, user, assignment);

        return createdAssignment;

    }

    public AssignmentDto fetchAssignmentDetails(Long assignmentId){

        Assignment assignment = assignmentRepository.findById(assignmentId).get();

        List<AttachmentDto> attachments = attachmentService.fetchAllAttachments(assignmentId);

        return AssignmentDto.builder()
                .id(assignmentId)
                .title(assignment.getTitle())
                .instructions(assignment.getInstructions())
                .points(assignment.getPoints())
                .dueDate()
                .attachments(attachments)
                .build();

    }



}
