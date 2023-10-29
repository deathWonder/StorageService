package com.example.storage.service;

import com.example.storage.model.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudyService {

    void init();

    void upload(MultipartFile file);

    void delete(String fileName);

    Resource download(String fileName);

    void edit(String fileName, String newFileName);

    List<FileResponse> getAllFiles(int limit);

}
