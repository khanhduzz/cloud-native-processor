package com.khanhduzz.image_worker.component;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khanhduzz.image_worker.entity.dto.ImageMessage;
import com.khanhduzz.image_worker.service.ImageService;

import io.awspring.cloud.sqs.annotation.SqsListener;

@ConditionalOnProperty(name = "app.sqs.enabled", havingValue = "true")
@Component
public class ImageSqsListener {
    private final ImageService imageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImageSqsListener(ImageService imageService) {
        this.imageService = imageService;
    }

    @SqsListener("${aws.sqs.queue-name}")
    public void listen(String rawMessage) {
        try {
            System.out.println("Raw: " + rawMessage);

            ImageMessage message = objectMapper.readValue(rawMessage, ImageMessage.class);

            System.out.println("Parsed: " + message);

            imageService.processImage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
