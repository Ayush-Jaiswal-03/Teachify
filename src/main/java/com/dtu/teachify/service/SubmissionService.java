package com.dtu.teachify.service;

import com.dtu.teachify.dto.*;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Submission;
import com.dtu.teachify.entity.SubmissionAttachment;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.enums.SubmissionStatus;
import com.dtu.teachify.exception.ApiException;
import com.dtu.teachify.repository.AssignmentRepository;
import com.dtu.teachify.repository.MemberRepository;
import com.dtu.teachify.repository.SubmissionAttachmentRepository;
import com.dtu.teachify.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAttachmentRepository submissionAttachmentRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;
    private final S3Service s3Service;

    public SubmissionDto submitAssignment(User user, Long assignmentId, MultipartFile[] files) throws IOException {

        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment Not Found ..."));

        Submission existingSubmission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, user.getId());

        if(existingSubmission != null){
            throw new ApiException(HttpStatus.CONFLICT, "Assignment Already Submitted ...");
        }

        SubmissionStatus submissionStatus = LocalDateTime.now().isAfter(assignment.getDueDate())
                ? SubmissionStatus.LATE
                : SubmissionStatus.ONTIME;

        Submission submission = Submission.builder()
                .student(user)
                .assignment(assignment)
                .status(submissionStatus)
                .points(assignment.getPoints())
                .submittedAt(LocalDateTime.now())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        attachmentService.uploadSubmissionAttachments(savedSubmission, files);

        List<SubmissionAttachmentDto> submissionAttachments = attachmentService.fetchAllSubmissionAttachments(submission.getId());

        return SubmissionDto.builder()
                .id(savedSubmission.getId())
                .status(savedSubmission.getStatus())
                .points(savedSubmission.getPoints())
                .submittedAt(savedSubmission.getSubmittedAt())
                .attachments(submissionAttachments).build();


    }


    public SubmissionDto getUserSubmission(Long userId, Long assignmentId){

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, userId);

        if(submission == null){
            return null;
        }

        List<SubmissionAttachmentDto> submissionAttachments = attachmentService.fetchAllSubmissionAttachments(submission.getId());

        return SubmissionDto.builder()
                .id(submission.getId())
                .status(submission.getStatus())
                .points(submission.getPoints())
                .submittedAt(submission.getSubmittedAt())
                .attachments(submissionAttachments)
                .build();

    }

    public SubmissionListResponse fetchAllSubmissionsForAssignment(Long assignmentId){

        List<SubmissionDto> submissions = submissionRepository.findAllSubmissions(assignmentId);

        List<SubmissionAttachment> attachments = submissionAttachmentRepository.findAttachments(assignmentId);

        List<SubmissionAttachmentDto> attachmentDtos = attachments.stream()
                .map(att -> {
                    String fileUrl = s3Service.generatePreSignedUrl(att.getFileKey());
                    return new SubmissionAttachmentDto(att.getId(), att.getSubmission().getId(), att.getFileName(), fileUrl);
                }).toList();

        Map<Long, List<SubmissionAttachmentDto>> attachmentMap =
                attachmentDtos.stream()
                        .collect(Collectors.groupingBy(
                                SubmissionAttachmentDto::getSubmissionId
                        ));

        submissions.forEach(submission -> {
            submission.setAttachments(
                    attachmentMap.getOrDefault(
                            submission.getId(),
                            Collections.emptyList()
                    )
            );
        });

        List<MemberDto> notSubmitted = memberRepository.findStudentsWhoHaveNotSubmitted(assignmentId);

        return SubmissionListResponse.builder()
                .submitted(submissions)
                .notSubmitted(notSubmitted)
                .build();

    }


}
