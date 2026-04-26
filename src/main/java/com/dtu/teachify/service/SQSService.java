package com.dtu.teachify.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SQSService {
    private final SqsClient sqsClient;
    private final NotificationService notificationService;

    private final String queueUrl = "https://sqs.ap-south-1.amazonaws.com/429234796370/teachify-notification-queue";

    @PostConstruct
    public void startPolling() {
        Executors.newSingleThreadExecutor().submit(this::poll);
    }

    private void poll() {
        while (true) {
            try {
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10) // batch
                        .waitTimeSeconds(20)     // long polling
                        .build();

                List<Message> messages = sqsClient.receiveMessage(request).messages();
                System.out.println("Message count: " + messages.size());

                for (Message msg : messages) {

                    try {
                        notificationService.processEvent(msg.body());
                        deleteMessage(msg);
                    } catch (Exception e) {
                        // log and skip (DLQ handles retries)
                        System.out.println("exception in inner try of sqs service");
                    }
                }

            } catch (Exception e) {
                // retry loop
            }
        }
    }

    private void deleteMessage(Message msg) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
                .build());
    }



}
