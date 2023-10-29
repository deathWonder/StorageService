package com.example.storage.configuration;

import com.example.storage.entity.UserData;
import com.example.storage.repository.UserRepository;
import com.example.storage.service.CloudyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FileUploadConfiguration implements CommandLineRunner {
    private final CloudyService service;

    public FileUploadConfiguration(CloudyService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.init();
    }
}
