package com.dtu.teachify.service;

import com.dtu.teachify.dto.AttachmentDto;
import com.dtu.teachify.dto.SubmissionAttachmentDto;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Attachment;
import com.dtu.teachify.entity.Submission;
import com.dtu.teachify.entity.SubmissionAttachment;
import com.dtu.teachify.repository.AttachmentRepository;
import com.dtu.teachify.repository.SubmissionAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final UserService userService;
    private final AttachmentRepository attachmentRepository;
    private final SubmissionAttachmentRepository submissionAttachmentRepository;
    private final S3Service s3Service;

    public void uploadAssignmentAttachments(Assignment assignment, MultipartFile[] files) throws IOException {

        List<Attachment> savedFiles = new ArrayList<>();

        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for (MultipartFile file : files) {
            String originalExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // Create unique file key for S3 (keeping original filename for reference)
            // fileLocation field mei yahi store karenge
            // also pre-signed url generate karne ke liye bhi
            String fileKey = UUID.randomUUID() + "__" + file.getOriginalFilename();

            // UPLOAD TO S3 instead of local
            s3Service.uploadFile(file, fileKey);

            Attachment attachment = Attachment.builder()
                    .fileName(file.getOriginalFilename())
                    .assignment(assignment)
                    .fileKey(fileKey) // Now stores S3 key instead of local path
                    .build();

            attachmentRepository.save(attachment);
        }

    }

    public void uploadSubmissionAttachments(Submission submission, MultipartFile[] files) throws IOException {

        List<Attachment> savedFiles = new ArrayList<>();

        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for (MultipartFile file : files) {
            String originalExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // Create unique file key for S3 (keeping original filename for reference)
            // fileLocation field mei yahi store karenge
            // also pre-signed url generate karne ke liye bhi
            String fileKey = UUID.randomUUID() + "__" + file.getOriginalFilename();

            // UPLOAD TO S3 instead of local
            s3Service.uploadFile(file, fileKey);

            SubmissionAttachment submissionAttachment = SubmissionAttachment.builder()
                    .submission(submission)
                    .fileName(file.getOriginalFilename())
                    .fileKey(fileKey).build();

            submissionAttachmentRepository.save(submissionAttachment);
        }

    }

    public List<AttachmentDto> fetchAllAttachments(Long assignmentId){
            List<Attachment> attachments = attachmentRepository.findByAssignmentId(assignmentId);

            return attachments.stream()
                    .map(att -> {
                        String fileUrl = s3Service.generatePreSignedUrl(att.getFileKey());
                        return new AttachmentDto(att.getId(), att.getFileName(), fileUrl);
                    }).toList();

    }

    public List<SubmissionAttachmentDto> fetchAllSubmissionAttachments(Long submissionId){
        List<SubmissionAttachment> submissionAttachments = submissionAttachmentRepository.findBySubmissionId(submissionId);

        return submissionAttachments.stream()
                .map(att -> {
                    String fileUrl = s3Service.generatePreSignedUrl(att.getFileKey());
                    return new SubmissionAttachmentDto(att.getId(), att.getFileName(), fileUrl);
                }).toList();

    }


}
