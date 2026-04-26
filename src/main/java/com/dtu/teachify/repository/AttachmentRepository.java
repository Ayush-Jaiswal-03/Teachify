package com.dtu.teachify.repository;

import com.dtu.teachify.dto.AttachmentDto;
import com.dtu.teachify.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByAssignmentId(Long assignmentId);
}
