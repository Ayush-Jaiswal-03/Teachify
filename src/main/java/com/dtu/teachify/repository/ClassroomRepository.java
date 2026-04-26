package com.dtu.teachify.repository;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.dto.MemberDto;
import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByJoinCode(String joinCode);

    @Query("""
        SELECT new com.dtu.teachify.dto.ClassroomDto(
            c.id, c.name, c.description, c.joinCode, m.role
        )
        FROM Member m
        JOIN m.classroom c
        WHERE m.user.id = :userId
    """)
    List<ClassroomDto> findAllClassroomsForUser(Long userId);

    @Query("""
            SELECT new com.dtu.teachify.dto.MemberDto(
                m.user.id, m.user.username, m.user.email, m.role
            )
            FROM Member m
            WHERE m.classroom.id = :classroomId
            """)
    List<MemberDto> findMembersByClassroomId(Long classroomId);
}
