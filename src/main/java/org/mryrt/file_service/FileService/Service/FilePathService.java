package org.mryrt.file_service.FileService.Service;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.FileService.Exceptions.FileProcessException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;
import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.*;

@Slf4j
@Component
public class FilePathService {

    @Value("${file.service.upload-dir}")
    private String UPLOAD_DIR;

    private Path getUserFolder(String username) {
        try {
            Path folder = Paths.get(UPLOAD_DIR).resolve(username).toAbsolutePath().normalize();
            if (!Files.isDirectory(folder)) {
                Files.createDirectories(folder);
            } else if (!Files.isReadable(folder)) {
                throw new FileProcessException(USER_DIRECTORY_NOT_READABLE, username);
            }
            return folder;
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, username);
        }
    }

    public void saveUserFile(MultipartFile file, FileMeta fileMeta, String username) {
        Path folder = getUserFolder(username);
        Path destination = folder.resolve(fileMeta.getUuid() + fileMeta.getExtension());
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new FileProcessException(FILE_COPY_ERROR, exception);
        }
    }

    public long getUserFolderSize(String username) {
        Path folder = getUserFolder(username);
        LongAdder folderSize = new LongAdder();

        try (Stream<Path> pathStream = Files.walk(folder)) {
            pathStream
                    .parallel()
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            folderSize.add(Files.size(file));
                        } catch (IOException exception) {
                            log.warn(FILE_SKIPPED.getFormattedMessage(file.getFileName().toString(), exception.getMessage()));
                        }
                    });
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, username);
        }

        return folderSize.sum();
    }

    public List<String> synchronizeUserFiles(List<String> filenameList, String username) {
        Path folder = getUserFolder(username);
        List<String> existingFiles = new ArrayList<>();

        try (Stream<Path> pathStream = Files.walk(folder)) {
            pathStream
                    .parallel()
                    .filter(path -> !path.equals(folder))
                    .forEach(file -> {
                        try {
                            String filename = file.getFileName().toString();
                            if (!filenameList.contains(filename)) {
                                Files.deleteIfExists(file);
                                log.warn(NOT_USER_FILE.getFormattedMessage(filename));
                                return;
                            }
                            if (Files.isDirectory(file)) {
                                Files.deleteIfExists(file);
                                log.warn(FILE_IS_DIRECTORY.getFormattedMessage(filename));
                                return;
                            }
                            if (!Files.isReadable(file)) {
                                Files.deleteIfExists(file);
                                log.warn(USER_DIRECTORY_NOT_READABLE.getFormattedMessage(filename));
                                return;
                            }
                            synchronized (existingFiles) {
                                existingFiles.add(filename);
                            }
                        } catch (IOException exception) {
                            log.warn(FILE_SKIPPED.getFormattedMessage(file.getFileName().toString(), exception.getMessage()));
                        }
                    });
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, username);
        }

        return existingFiles;
    }

}
