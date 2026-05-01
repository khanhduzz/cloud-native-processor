package com.khanhduzz.image_worker.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageMessage {
    @JsonProperty("ImageId")
    private String imageId;
    @JsonProperty("S3Key")
    private String s3Key;
    @JsonProperty("BucketName")
    private String bucketName;

    public ImageMessage() {
    }

    public ImageMessage(String imageId, String s3Key, String bucket) {
        this.imageId = imageId;
        this.s3Key = s3Key;
        this.bucketName = bucket;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String toString() {
        return "ImageMessage [imageId=" + imageId + ", s3Key=" + s3Key + ", bucketName=" + bucketName + "]";
    }

}
