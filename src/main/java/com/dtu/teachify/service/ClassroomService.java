package com.dtu.teachify.service;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.dto.MemberDto;
import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.Member;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.enums.Role;
import com.dtu.teachify.exception.ApiException;
import com.dtu.teachify.repository.AssignmentRepository;
import com.dtu.teachify.repository.ClassroomRepository;
import com.dtu.teachify.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final AssignmentRepository assignmentRepository;
    private final MemberRepository memberRepository;

    public ClassroomDto create(User user, ClassroomDto classroomInfo) {

        try {
            String joinCode = UUID.randomUUID().toString().substring(0, 8);

            Classroom createClassroom = Classroom.builder()
                    .name(classroomInfo.getName())
                    .description(classroomInfo.getDescription())
                    .createdBy(user.getId())
                    .joinCode(joinCode)
                    .createdAt(LocalDateTime.now())
                    .build();

            classroomRepository.save(createClassroom);

            Member createMember = Member.builder()
                    .classroom(createClassroom) //*** internally sirf id store hoti hai
                    .user(user)
                    .role(Role.TEACHER)
                    .joinedAt(LocalDateTime.now())
                    .build();

            memberRepository.save(createMember);

            return ClassroomDto.builder()
                    .id(createClassroom.getId())
                    .name(createClassroom.getName())
                    .description(createClassroom.getDescription())
                    .joinCode(createClassroom.getJoinCode())
                    .role(Role.TEACHER)
                    .build();

        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating a new Classroom ...");
        }
    }

    public ClassroomDto joinClassroom(User currentUser, String joinCode) {

        Classroom classroom = classroomRepository.findByJoinCode(joinCode).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Classroom not found with join code ..."));

        boolean userAlreadyMember = memberRepository.existsByClassroomIdAndUserId(classroom.getId(), currentUser.getId());

        if(userAlreadyMember){
            throw new ApiException(HttpStatus.BAD_REQUEST, "User already exists in the classroom ...");
        }

        Member member = Member.builder()
                .classroom(classroom)
                .role(Role.STUDENT)
                .user(currentUser)
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);

        return ClassroomDto.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .description(classroom.getDescription())
                .role(Role.STUDENT)
                .joinCode(classroom.getJoinCode())
                .build();
    }

    public List<ClassroomDto> getAllClassroomsForUser(User user){
        return classroomRepository.findAllClassroomsForUser(user.getId());
    }

    public List<Assignment> getAllAssignments(Long classroomId){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Classroom not found"));
        return assignmentRepository.findByClassroomId(classroom.getId());
    }

    public List<MemberDto> findAllMembers(Long classroomId){
        return classroomRepository.findMembersByClassroomId(classroomId);
    }


}
