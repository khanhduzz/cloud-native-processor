package com.khanhduzz.image_worker.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.khanhduzz.image_worker.entity.ImageResponse;
import com.khanhduzz.image_worker.repository.ImageRepository;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class ImageQueryService {

    private final ImageRepository repository;
    private final S3Presigner presigner;

    public ImageQueryService(ImageRepository repository, S3Presigner presigner) {
        this.repository = repository;
        this.presigner = presigner;
    }

    public List<ImageResponse> getAllImages() {
        return repository.findAll().stream().map(entity -> {

            String thumbnailUrl = generatePresignedUrl(
                    entity.getBucket(),
                    entity.getThumbnailFileName());

            return new ImageResponse(
                    entity.getId().toString(),
                    thumbnailUrl);

        }).toList();
    }

    public Map<String, String> getDownloadUrls(Long imageIds) {
        var img = repository.findById(imageIds).orElseThrow();

        String url = generateDownloadUrl(
                img.getBucket(),
                img.getThumbnailFileName());

        return Map.of("url", url);
    }

    private String generatePresignedUrl(String bucket, String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    public String generateDownloadUrl(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition(
                        "attachment; filename=\"" + key + "\"")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(request)
                .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
}