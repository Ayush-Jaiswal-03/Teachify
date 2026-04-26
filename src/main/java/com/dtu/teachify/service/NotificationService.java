package com.dtu.teachify.service;

import com.dtu.teachify.dto.AssignmentPostedEvent;
import com.dtu.teachify.dto.MemberDto;
import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.Notification;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.repository.ClassroomRepository;
import com.dtu.teachify.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClassroomRepository classroomRepository;

    private AssignmentPostedEvent getEventFromJson(String messageJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(messageJson);

        System.out.println(root);

        String innerMessage = root.get("Message").asText();

        System.out.println(innerMessage);

        return objectMapper.readValue(innerMessage, AssignmentPostedEvent.class);

    }

    public void  processEvent(String messageJson) throws Exception{

//        System.out.println("Inside notification service ............");

        AssignmentPostedEvent event = getEventFromJson(messageJson);

        Long classroomId = event.getClassroomId();

        List<MemberDto> members = classroomRepository.findMembersByClassroomId(classroomId);

        List<Long> memberIds = members.stream().map(MemberDto::getId).toList();

        List<Notification> batch = new ArrayList<>();

        for (Long id : memberIds) {

            String message = "%s posted a new assignment in %s"
                    .formatted(event.getPostedBy(), event.getClassroomName());

            Notification n = Notification.builder()
                    .userId(id)
                    .classroomId(classroomId)
                    .isRead(false)
                    .type("ASSIGNMENT")
                    .message(message)
                    .build();

//            System.out.println("notification: " + n);

            batch.add(n);

//            // Redis increment
//            redisTemplate.opsForValue().increment("notif_count:" + userId);
        }

        notificationRepository.saveAll(batch);
    }
}
