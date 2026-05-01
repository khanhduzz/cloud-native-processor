package com.khanhduzz.image_worker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "images")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bucket;

    private String originalFileName;
    private String thumbnailFileName;

    private LocalDateTime createdAt;

    public ImageEntity() {
    }

    public ImageEntity(Long id, String bucket, String originalFileName, String thumbnailFileName,
            LocalDateTime createdAt) {
        this.id = id;
        this.bucket = bucket;
        this.originalFileName = originalFileName;
        this.thumbnailFileName = thumbnailFileName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public void setThumbnailFileName(String thumbnailFileName) {
        this.thumbnailFileName = thumbnailFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ImageEntity [id=" + id + ", bucket=" + bucket + ", originalFileName=" + originalFileName
                + ", thumbnailFileName=" + thumbnailFileName + ", createdAt=" + createdAt + "]";
    }
}
