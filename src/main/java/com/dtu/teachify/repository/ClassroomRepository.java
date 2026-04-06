package com.dtu.teachify.repository;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByJoinCode(String joinCode);

    @Query("""
        SELECT new com.dtu.teachify.dto.ClassroomDTO(
            c.name, c.description, c.joinCode, m.role
        )
        FROM Member m
        JOIN m.classroom c
        WHERE m.user.id = :userId
    """)
    List<ClassroomDto> findAllClassroomsForUser(Long userId);
}
