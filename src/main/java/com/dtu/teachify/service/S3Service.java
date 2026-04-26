package com.dtu.teachify.service;

//import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    // Constructor injection
    public S3Service(S3Client s3Client,
                     S3Presigner s3Presigner,
                     @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String fileKey) {

        try{

            //getting the file in bytes
            byte[] fileBytes = file.getBytes();
            //Creating put object request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            //upload it on S3 bucket
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

//            log.info("File uploaded successfully {}",fileKey);
            return fileKey;

        }catch (Exception e){
//            log.error("Error uploading file {}",e.getMessage());
            throw new RuntimeException("Failed to upload file to S3");

        }
    }


    public String generatePreSignedUrl(String fileKey){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            // Generate presigned URL valid for 1 hour
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofHours(1))
                            .getObjectRequest(getObjectRequest)
                            .build()
            );

            return presignedRequest.url().toString();



        } catch (Exception e) {
//            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate download URL", e);

        }
    }


    public void deleteFile(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
//            log.info("File deleted from S3 with key: {}", fileKey);

        } catch (Exception e) {
//            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }




}
