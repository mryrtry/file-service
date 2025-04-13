package org.mryrt.file_service.FileService.Service;

import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Annotation.FileSynchronization;
import org.mryrt.file_service.FileService.Exceptions.FileProcessException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;

@Service
@Slf4j
public class FileService {

    @Value("${file.service.max-file-size}")
    private int MAX_FILE_SIZE;

    @Value("${file.service.max-folder-size}")
    private long MAX_FOLDER_SIZE;

    @Autowired
    FileMetaRepository fileMetaRepository;

    @Autowired
    UserService userService;

    @Autowired
    FilePathService filePathService;

    @Autowired
    FileMetaService fileMetaService;

    @Autowired
    private ResourceLoader resourceLoader;

    private void assertFileNotEmpty(MultipartFile file) {
        if (file.isEmpty())
            throw new FileProcessException(FILE_IS_EMPTY);
    }

    private void assertFileSize(long fileSize, String username) {
        if (fileSize > MAX_FILE_SIZE)
            throw new FileProcessException(FILE_SIZE_TOO_LARGE);
        if (fileSize + filePathService.getUserFolderSize(username) > MAX_FOLDER_SIZE)
            throw new FileProcessException(NOT_ENOUGH_SPACE, username);
    }

    private void assertUuid(String uuid) {
        if (!uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
            throw new FileProcessException(INVALID_FILE_UUID);
    }

    private FileMeta getFileMeta(String uuid, UserDTO user) {
        return fileMetaRepository.findByUuidAndOwnerId(uuid, user.getId())
                .orElseThrow(() -> new FileProcessException(UUID_NOT_EXIST, uuid, user.getUsername()));
    }

    private HttpHeaders getHttpHeaders(String filename) {
        String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(encodedFilename));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return headers;
    }

    @FileSynchronization
    public FileMetaDTO uploadFile(MultipartFile file) {
        assertFileNotEmpty(file);
        UserDTO user = userService.getAuthUser();
        assertFileSize(file.getSize(), user.getUsername());
        FileMeta fileMeta = fileMetaService.getFileMeta(user, file);
        filePathService.saveUserFile(file, fileMeta, user.getUsername());
        return new FileMetaDTO(fileMetaRepository.save(fileMeta));
    }

    @FileSynchronization
    public List<FileMetaDTO> getFiles() {
        return fileMetaRepository
                .findAllByOwnerId(userService.getAuthUser().getId())
                .stream()
                .map(FileMetaDTO::new)
                .toList();
    }

    @FileSynchronization
    public Pair<Resource, HttpHeaders> getFile(String uuid) {
        assertUuid(uuid);
        UserDTO user = userService.getAuthUser();
        FileMeta fileMeta = getFileMeta(uuid, user);
        return Pair.of(filePathService.getUserFile(fileMeta, user.getUsername()), getHttpHeaders(fileMeta.getName()));
    }

    @FileSynchronization
    public FileMetaDTO deleteFile(String uuid) {
        assertUuid(uuid);
        UserDTO user = userService.getAuthUser();
        FileMeta fileMeta = getFileMeta(uuid, user);
        filePathService.deleteUserFile(fileMeta, user.getUsername());
        fileMetaRepository.delete(fileMeta);
        return new FileMetaDTO(fileMeta);
    }

}
