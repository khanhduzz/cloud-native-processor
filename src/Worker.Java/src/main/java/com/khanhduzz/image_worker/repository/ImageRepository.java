package com.khanhduzz.image_worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khanhduzz.image_worker.entity.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
