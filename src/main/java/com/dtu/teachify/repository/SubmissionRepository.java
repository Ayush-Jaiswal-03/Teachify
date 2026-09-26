package com.dtu.teachify.repository;

import com.dtu.teachify.dto.AttachmentDto;
import com.dtu.teachify.dto.SubmissionDto;
import com.dtu.teachify.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("""
            SELECT new com.dtu.teachify.dto.SubmissionDto(
            s.id, u.id, u.username, s.status, s.points, s.submittedAt, s.teacherComment
            )
            from Submission s
            JOIN s.student u
            where s.assignment.id = :assignmentId
            """)
    List<SubmissionDto> findAllSubmissions(Long assignmentId);


}
