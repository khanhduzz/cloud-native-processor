package com.khanhduzz.image_worker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.khanhduzz.image_worker.entity.ImageResponse;
import com.khanhduzz.image_worker.service.ImageQueryService;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageQueryService service;

    public ImageController(ImageQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ImageResponse> getImages() {
        return service.getAllImages();
    }

    @GetMapping("{id}/download")
    public Map<String, String> getDownloadUrl(@PathVariable Long id) {
        return service.getDownloadUrls(id);
    }
}