package com.dtu.teachify.controller;

import com.dtu.teachify.dto.ClassroomDto;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.User;
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
    public ResponseEntity<Classroom> createClassroom(@RequestBody ClassroomDto classroomInfo) {

        if (classroomInfo == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        User currentUser = userService.getCurrentUser();
        Classroom classroom = classroomService.create(currentUser, classroomInfo);

        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinClassroom(@RequestBody String joinCode){

        User currentUser = userService.getCurrentUser();

        classroomService.joinClassroom(currentUser, joinCode);

        return new ResponseEntity<>("Classroom joined Successfully ...", HttpStatus.OK);

    }

    @GetMapping("/get")
    public List<ClassroomDto> getAllClassrooms(){

        User currentUser = userService.getCurrentUser();

        return classroomService.getAllClassroomsForUser(currentUser);

    }



//    @DeleteMapping("/delete")
//    public void removeUserFromClassroom(){
//
//    }

}
