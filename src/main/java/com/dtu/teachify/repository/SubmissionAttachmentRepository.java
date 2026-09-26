package com.dtu.teachify.repository;

import com.dtu.teachify.dto.AttachmentDto;
import com.dtu.teachify.dto.SubmissionAttachmentDto;
import com.dtu.teachify.entity.SubmissionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionAttachmentRepository extends JpaRepository<SubmissionAttachment, Long> {

    List<SubmissionAttachment> findBySubmissionId(Long submissionId);

    @Query("""
    SELECT a
    FROM SubmissionAttachment a
    WHERE a.submission.assignment.id = :assignmentId
""")
    List<SubmissionAttachment> findAttachments(Long assignmentId);

}
