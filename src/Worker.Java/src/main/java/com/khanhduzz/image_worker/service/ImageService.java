package com.khanhduzz.image_worker.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.khanhduzz.image_worker.entity.ImageEntity;
import com.khanhduzz.image_worker.entity.dto.ImageMessage;
import com.khanhduzz.image_worker.repository.ImageRepository;

import net.coobird.thumbnailator.Thumbnails;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ImageService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    public ImageService(S3Client s3Client, ImageRepository imageRepository) {
        this.s3Client = s3Client;
        this.imageRepository = imageRepository;
    }

    public void processImage(String bucket, String s3Key, String imageId) {
        processImage(new ImageMessage(bucket, s3Key, imageId));
    }

    public void processImage(ImageMessage message) {
        Path tempFile = null;
        Path thumbnail = null;

        try {
            String bucket = message.getBucketName();
            String s3Key = message.getS3Key();
            String imageId = message.getImageId();

            // 1. Download image
            InputStream inputStream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Key)
                            .build());

            String safeName = s3Key.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            tempFile = Files.createTempFile("tempImage-", safeName);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            // 2. Create thumbnail
            thumbnail = createThumbnail(tempFile);

            String thumbnailKey = "thumb-" + s3Key;

            // 3. Upload thumbnail
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(thumbnailKey)
                            .contentType("image/jpeg")
                            .build(),
                    thumbnail);

            // 5. Save to DB
            ImageEntity entity = new ImageEntity();
            // entity.setId(imageId);
            entity.setBucket(bucket);
            entity.setOriginalFileName(s3Key);
            entity.setThumbnailFileName(thumbnailKey);
            entity.setCreatedAt(LocalDateTime.now());

            imageRepository.save(entity);

            System.out.println("Saved image + thumbnail: " + imageId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // cleanup temp files
            try {
                if (tempFile != null)
                    Files.deleteIfExists(tempFile);
                if (thumbnail != null)
                    Files.deleteIfExists(thumbnail);
            } catch (Exception ignored) {
            }
        }
    }

    private Path createThumbnail(Path original) throws Exception {
        Path output = Files.createTempFile("thumb-", ".jpg");

        Thumbnails.of(original.toFile())
                .size(200, 200)
                .toFile(output.toFile());

        return output;
    }
}