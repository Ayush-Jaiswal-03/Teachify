package com.dtu.teachify.repository;

import com.dtu.teachify.dto.AssignmentSummaryDto;
import com.dtu.teachify.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassroomId(Long classroomId);

    @Query("""
            SELECT new com.dtu.teachify.dto.AssignmentSummaryDto(
            a.id, a.title, COUNT(s.id)
            )
            FROM Assignment a
            LEFT JOIN Submission s
            ON s.assignment.id = a.id
            
            WHERE a.classroom.id = :classroomId
            GROUP BY a.id, a.title
            ORDER BY a.createdAt DESC
            """)
    List<AssignmentSummaryDto> fetchAssignmentsWithSubmissionCount(Long classroomId);
}
