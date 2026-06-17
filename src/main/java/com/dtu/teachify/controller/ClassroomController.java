package com.dtu.teachify.controller;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.dto.MemberDto;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.response.ApiResponse;
import com.dtu.teachify.service.ClassroomService;
import com.dtu.teachify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final UserService userService;
    private final ClassroomService classroomService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ClassroomDto>> createClassroom(@RequestBody ClassroomDto classroomInfo) {

        if (classroomInfo == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        User currentUser = userService.authenticateAndGetUser();
        ClassroomDto createdClassroom = classroomService.create(currentUser, classroomInfo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Classroom created Successfully ...", createdClassroom));
    }

    @GetMapping("/{joinCode}/join")
    public ResponseEntity<ApiResponse<ClassroomDto>> joinClassroom(@PathVariable String joinCode){

        User currentUser = userService.authenticateAndGetUser();

        ClassroomDto joinedClassroom = classroomService.joinClassroom(currentUser, joinCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Classroom joined Successfully ...", joinedClassroom));
    }

    @GetMapping("/get")
    public List<ClassroomDto> getAllClassrooms(){
        User currentUser = userService.authenticateAndGetUser();
        return classroomService.getAllClassroomsForUser(currentUser);
    }

    @GetMapping("/{classroomId}/assignments")
    public List<Assignment> fetchAllAssignments(@PathVariable Long classroomId){
        User currentUser = userService.authenticateAndGetUser();
        return classroomService.getAllAssignments(classroomId);
    }

    @GetMapping("/{classroomId}/users")
    public List<MemberDto> getAllUsersForClassroom(@PathVariable Long classroomId){
        return classroomService.findAllMembers(classroomId);
    }

}
