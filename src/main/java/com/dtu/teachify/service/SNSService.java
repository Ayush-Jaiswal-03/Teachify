package com.dtu.teachify.service;

import com.dtu.teachify.dto.AssignmentPostedEvent;
import com.dtu.teachify.entity.Assignment;
import com.dtu.teachify.entity.Classroom;
import com.dtu.teachify.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
public class SNSService {

    private final SnsClient snsClient;
    private final String topicArn = "arn:aws:sns:ap-south-1:429234796370:teachify-notification-events";

    public void publishAssignmentPosted(Classroom classroom, User user, Assignment assignment) throws JsonProcessingException {

        System.out.println("inside sns service ......");

        ObjectMapper objectMapper = new ObjectMapper();

        AssignmentPostedEvent event =  new AssignmentPostedEvent(
                "ASSIGNMENT_POSTED",
                classroom.getId(),
                classroom.getName(),
                assignment.getId(),
                user.getUsername()
        );

        String payload = objectMapper.writeValueAsString(event);

        System.out.println(payload);

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(payload)
                .build();

        snsClient.publish(request);
    }



}
