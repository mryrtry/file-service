package org.mryrt.file_service.FileService.Service;

import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Annotation.FileSync;
import org.mryrt.file_service.FileService.Exceptions.FileProcessException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;

import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;

@Service
@Slf4j
@TrackExecutionTime
public class FileService {

    @Value("${file.service.max-file-size}")
    private DataSize MAX_FILE_SIZE;

    @Value("${file.service.max-folder-size}")
    private DataSize MAX_FOLDER_SIZE;

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

    private MultipartFile getFile(MultipartFile[] files) {
        if (files.length != 1)
            throw new FileProcessException(FILES_LIMIT_EXCEEDED);
        return files[0];
    }

    private void assertFileNotEmpty(MultipartFile file) {
        if (file.isEmpty())
            throw new FileProcessException(FILE_IS_EMPTY);
    }

    private void assertFileSize(long fileSize, long userId) {
        if (fileSize > MAX_FILE_SIZE.toBytes())
            throw new FileProcessException(FILE_SIZE_TOO_LARGE);
        if (fileSize + filePathService.getUserFolderSize(userId) > MAX_FOLDER_SIZE.toBytes())
            throw new FileProcessException(NOT_ENOUGH_SPACE, userId);
    }

    private void assertUuid(String uuid) {
        if (!uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
            throw new FileProcessException(INVALID_FILE_UUID);
    }

    private FileMeta getFileMeta(String uuid, long userId) {
        return fileMetaRepository.findByUuidAndOwnerId(uuid, userId)
                .orElseThrow(() -> new FileProcessException(UUID_NOT_EXIST, uuid, userId));
    }

    private HttpHeaders getHttpHeaders(String filename) {
        String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(encodedFilename));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return headers;
    }

    public FileMetaDTO uploadFile(MultipartFile[] files) {
        MultipartFile file = getFile(files);
        assertFileNotEmpty(file);
        long userId = userService.getAuthUserId();
        assertFileSize(file.getSize(), userId);
        FileMeta fileMeta = fileMetaService.getFileMeta(userId, file);
        filePathService.saveUserFile(file, fileMeta.getDiskName(), userId);
        return new FileMetaDTO(fileMetaRepository.save(fileMeta));
    }

    @FileSync
    public List<FileMetaDTO> getFiles() {
        return fileMetaRepository
                .findAllByOwnerId(userService.getAuthUserId())
                .stream()
                .map(FileMetaDTO::new)
                .toList();
    }

    @FileSync
    public Pair<Resource, HttpHeaders> getFile(String uuid) {
        assertUuid(uuid);
        long userId = userService.getAuthUserId();
        FileMeta fileMeta = getFileMeta(uuid, userId);
        return Pair.of(filePathService.getUserFile(fileMeta.getDiskName(), userId), getHttpHeaders(fileMeta.getName()));
    }

    @FileSync
    public FileMetaDTO deleteFile(String uuid) {
        assertUuid(uuid);
        long userId = userService.getAuthUserId();
        FileMeta fileMeta = getFileMeta(uuid, userId);
        filePathService.deleteUserFile(fileMeta.getDiskName(), userId);
        fileMetaRepository.delete(fileMeta);
        return new FileMetaDTO(fileMeta);
    }

}
