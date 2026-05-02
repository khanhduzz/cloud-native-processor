package com.khanhduzz.image_worker.component;

import java.net.URI;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khanhduzz.image_worker.entity.dto.ImageMessage;
import com.khanhduzz.image_worker.service.ImageService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class ImageLambdaHandler implements RequestHandler<SQSEvent, String> {

    private final ImageService imageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handleRequest(SQSEvent event, Context context) {

        event.getRecords().forEach(record -> {
            try {
                String body = record.getBody();

                ImageMessage message = objectMapper.readValue(body, ImageMessage.class);

                System.out.println("Processing via Lambda: " + message);

                imageService.processImage(
                        message.getBucketName(),
                        message.getS3Key(),
                        message.getImageId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "Processed";
    }

    public ImageLambdaHandler() {
        // manual wiring for Lambda
        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.of("us-east-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")))
                .forcePathStyle(true)
                .build();

        this.imageService = new ImageService(s3Client, null);
    }
}