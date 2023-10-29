package com.example.storage.controller;

import com.example.storage.model.FileResponse;
import com.example.storage.service.CloudyService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class CloudyController {
    private final CloudyService service;

    public CloudyController(CloudyService service) {
        this.service = service;

    }


    @PostMapping("/file")
    public ResponseEntity<Void> upload(@RequestParam MultipartFile file) {
        service.upload(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/file")
    public ResponseEntity<Void> delete(@RequestParam String filename) {
        service.delete(filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/file")
    public ResponseEntity<Resource> download(@RequestParam String filename) {
        return ResponseEntity.ok(service.download(filename));
    }


    @PutMapping("/file")
    public ResponseEntity<Void> edit(@RequestParam String filename,
                                     @RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("filename");
        service.edit(filename, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getAllFiles(@RequestParam int limit) {
        return ResponseEntity.ok(service.getAllFiles(limit));
    }


}
