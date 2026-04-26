package com.dtu.teachify.repository;

import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.Member;
import com.dtu.teachify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByClassroomIdAndUserId(Long classroomId, Long userId);

    Member findByClassroomIdAndUserId(Long classroomId, Long userId);

}