package com.dtu.teachify.service;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.Member;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.enums.Role;
import com.dtu.teachify.exception.BadRequestException;
import com.dtu.teachify.exception.InternalServerException;
import com.dtu.teachify.exception.ResourceNotFoundException;
import com.dtu.teachify.repository.ClassroomRepository;
import com.dtu.teachify.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;

    public Classroom create(User user, ClassroomDto classroomInfo) {

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

            return createClassroom;
        } catch (Exception e) {
            throw new InternalServerException("Error while creating a new Classroom ...", e);
        }
    }

    public void joinClassroom(User currentUser, String joinCode) {

        Classroom classroom = classroomRepository.findByJoinCode(joinCode).orElseThrow(() -> new ResourceNotFoundException("Classroom not found with join code ..."));

        boolean userAlreadyMember = memberRepository.existsByClassroomIdAndUserId(classroom.getId(), currentUser.getId());
        if(userAlreadyMember){
            throw new BadRequestException("User already exists in the classroom ...");
        }

        Member member = Member.builder()
                .classroom(classroom)
                .role(Role.STUDENT)
                .user(currentUser)
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    public List<ClassroomDto> getAllClassroomsForUser(User user){
        return classroomRepository.findAllClassroomsForUser(user.getId());
    }



}
