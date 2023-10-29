package com.example.storage.service;

import com.example.storage.entity.FileData;
import com.example.storage.entity.UserData;
import com.example.storage.exception.ErrorInputDataException;
import com.example.storage.model.FileResponse;
import com.example.storage.repository.FileRepository;
import com.example.storage.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CloudyServiceImpl implements CloudyService {

    //в этой папке будут храниться файлы пользователей по их логинам
    private final Path path = Paths.get("fileStorage");
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    public CloudyServiceImpl(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    //метод создания папки-хранилища
    @Override
    public void init() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize folder for upload!");
            }
        }
    }

    @Override
    public void upload(MultipartFile file) {
        //получаем пользователя
        UserData user = getUser(SecurityContextHolder.getContext().getAuthentication());
        //путь куда загружать файл
        Path userPath = Paths.get(path.toAbsolutePath() + "/" + user.getId());
        try {
            //проверяем существует ли папка пользователя
            if (Files.notExists(userPath)) {
                Files.createDirectory(userPath);
            }
            // получаем разрешение файла
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            //сохраняем файл по пути в хранилище
            Files.copy(file.getInputStream(), userPath.resolve(file.getOriginalFilename()));
            //добавляем в базу данных
            fileRepository.save(FileData.builder()
                    .name(FilenameUtils.removeExtension(file.getOriginalFilename()))
                    .type(extension)
                    .size((int) file.getSize())
                    .userId(user)
                    .build());
        } catch (IOException e) {
            throw new ErrorInputDataException("Could not store the file.");
        }
    }

    @Override
    public void delete(String fileName) {
        //получаем пользователя
        UserData user = getUser(SecurityContextHolder.getContext().getAuthentication());
        //получаем файл по пользователю, имени файла и если он не удален
        Optional<FileData> fileData = fileRepository.findFileDataByUserIdAndNameAndRemote(user, FilenameUtils.removeExtension(fileName), false);
        //проверяем существует ли файл
        if (fileData.isPresent()) {
            FileData file = fileData.get();
            //создаем путь к файлу
            Path filePath = Paths.get(path.toAbsolutePath() + "/" + user.getId() + "/" + fileName);
            try {
                //удаляем файл
                Files.delete(filePath);
            } catch (IOException e) {
                throw new ErrorInputDataException("Could not delete the file.");
            }
            //добавляем метку базе данных, что файл удален
            file.setRemote(true);
            fileRepository.save(file);
        } else {
            throw new ErrorInputDataException("The file do not exists.");
        }
    }

    @Override
    public Resource download(String fileName) {
        //получаем пользователя
        UserData user = getUser(SecurityContextHolder.getContext().getAuthentication());
        //получаем файл по пользователю, имени файла и если он не удален
        Optional<FileData> fileData = fileRepository.findFileDataByUserIdAndNameAndRemote(user, FilenameUtils.removeExtension(fileName), false);
        //проверяем существует ли файл
        if (fileData.isPresent()) {

            //создаем путь к файлу
            Path filePath = Paths.get(path.toAbsolutePath() + "/" + user.getId() + "/" + fileName);

            try {
                //возвращаем файл
                return new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new ErrorInputDataException("Could not read the file.");
            }
        } else {
            throw new ErrorInputDataException("The file do not exists.");
        }
    }

    @Override
    public void edit(String fileName, String newFileName) {
        //получаем пользователя
        UserData user = getUser(SecurityContextHolder.getContext().getAuthentication());
        //получаем файл по пользователю, имени файла и если он не удален
        Optional<FileData> fileData = fileRepository.findFileDataByUserIdAndNameAndRemote(user, FilenameUtils.removeExtension(fileName), false);
        //проверяем существует ли файл
        if (fileData.isPresent()) {
            FileData file = fileData.get();
            //создаем путь к файлу
            Path filePath = Paths.get(path.toAbsolutePath() + "/" + user.getId() + "/" + fileName);

            try {
                //меняем имя файла
                Files.move(filePath, filePath.resolveSibling(newFileName));
            } catch (IOException e) {
                throw new ErrorInputDataException("Could not edit the file.");
            }
            //меняем имя в базе данных
            file.setName(FilenameUtils.removeExtension(newFileName));
            fileRepository.save(file);
        } else {
            throw new ErrorInputDataException("The file do not exists.");
        }
    }

    @Override
    public List<FileResponse> getAllFiles(int limit) {
        if(limit<=0) throw new ErrorInputDataException("Unsuitable value!.");
        List<FileResponse> result = new ArrayList<>();

        //получаем пользователя
        UserData user = getUser(SecurityContextHolder.getContext().getAuthentication());

        //получаем список файлов по логину первые несколько и не удаленные
        List<FileData> list = fileRepository.findFileDataByUserIdAndRemoteOrderByIdDesc(user, false, PageRequest.of(0, limit));
        //подоготавливаем список файлов
        for (FileData file : list) {
            FileResponse fileResponse = new FileResponse(file.getName()+"."+file.getType(), file.getSize());
            result.add(fileResponse);
        }

        return result;
    }

    private UserData getUser(Authentication authentication){
        Optional<UserData> userData = userRepository.findByLogin(authentication.getName());
        UserData user;
        if(userData.isPresent()){
            user = userData.get();
        } else throw new ErrorInputDataException("Authentication error");
        return user;
    }

}
