package com.dtu.teachify.repository;

import com.dtu.teachify.dto.MemberDto;
import com.dtu.teachify.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByClassroomIdAndUserId(Long classroomId, Long userId);

    Member findByClassroomIdAndUserId(Long classroomId, Long userId);

    @Query("""
    SELECT new com.dtu.teachify.dto.MemberDto(
        m.user.id,
        m.user.username,
        m.user.email,
        m.role
    )
    FROM Member m
    WHERE m.classroom.id = (
        SELECT a.classroom.id
        FROM Assignment a
        WHERE a.id = :assignmentId
    )
    AND m.role = com.dtu.teachify.enums.Role.STUDENT
    AND NOT EXISTS (
        SELECT 1
        FROM Submission s
        WHERE s.assignment.id = :assignmentId
        AND s.student.id = m.user.id
    )
""")
    List<MemberDto> findStudentsWhoHaveNotSubmitted(Long assignmentId);

    @Query("""
            SELECT COUNT(m)
            FROM Member m
            WHERE m.classroom.id = :classroomId
            AND m.role = com.dtu.teachify.enums.Role.STUDENT
            """)
    int countStudentsByClassroomId(Long classroomId);

}