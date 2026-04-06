package com.dtu.teachify.repository;

import com.dtu.teachify.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByClassroomIdAndUserId(Long classroomId, Long userId);
}
