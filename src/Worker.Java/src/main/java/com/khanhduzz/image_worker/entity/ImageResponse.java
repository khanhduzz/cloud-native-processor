package com.khanhduzz.image_worker.entity;

public record ImageResponse(
        String id,
        String originalUrl,
        String thumbnailUrl) {
}
