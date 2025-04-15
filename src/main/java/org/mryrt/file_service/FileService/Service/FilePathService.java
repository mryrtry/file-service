package org.mryrt.file_service.FileService.Service;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Exceptions.FileProcessException;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;
import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.*;

@Slf4j
@Component
@TrackExecutionTime
public class FilePathService {

    @Value("${file.service.upload-dir}")
    private String UPLOAD_DIR;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    UserService userService;

    private Path getUserFolder(long userId) {
        try {
            Path folder = Paths.get(UPLOAD_DIR).resolve(String.valueOf(userId)).toAbsolutePath().normalize();
            if (!Files.isDirectory(folder)) {
                Files.createDirectories(folder);
            } else if (!Files.isReadable(folder)) {
                throw new FileProcessException(USER_DIRECTORY_NOT_READABLE, userId);
            }
            return folder;
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        }
    }

    private Path getBaseFolder() {
        Path folder = Paths.get(UPLOAD_DIR).normalize();
        try {
            if (Files.isDirectory(folder))
                Files.createDirectories(folder);
        } catch (IOException ignored) {
        }
        return folder;
    }

    public long getUserFolderSize(long userId) {
        Path folder = getUserFolder(userId);
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
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        } finally {
            Cleaner.create().register(this, () -> System.gc());
        }

        return folderSize.sum();
    }

    public void saveUserFile(MultipartFile file, String filename, long userId) {
        Path folder = getUserFolder(userId);
        Path destination = folder.resolve(filename).normalize();
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileSystemException exception) {
            if (exception.getMessage().contains("No space left on device"))
                throw new FileProcessException(NOT_ENOUGH_SPACE, userId, exception);
            throw new FileProcessException(FILE_COPY_ERROR, exception);
        } catch (IOException exception) {
            throw new FileProcessException(FILE_COPY_ERROR, exception);
        }
    }

    public void deleteUserFile(String filename, long userId) {
        Path folder = getUserFolder(userId);
        Path destination = folder.resolve(filename).normalize();
        try {
            if (!Files.deleteIfExists(destination))
                throw new FileProcessException(USER_FILE_NOT_EXIST, userId, filename);
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, userId);
        }
    }

    public List<String> syncingUserFiles(List<String> filenameList, long userId) {
        Path folder = getUserFolder(userId);
        List<String> existingFiles = Collections.synchronizedList(new ArrayList<>());

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
                            existingFiles.add(filename);
                        } catch (IOException exception) {
                            log.warn(FILE_SKIPPED.getFormattedMessage(file.getFileName().toString(), exception.getMessage()));
                        }
                    });
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        } finally {
            Cleaner.create().register(this, () -> System.gc());
        }

        return existingFiles;
    }

    public Resource getUserFile(String filename, long userId) {
        Path folder = getUserFolder(userId);
        Path destination = folder.resolve(filename).normalize();
        Resource resource = resourceLoader.getResource("file:%s".formatted(destination.toAbsolutePath().toString()));
        if (!resource.exists())
            throw new FileProcessException(USER_FILE_NOT_EXIST, userId, filename);
        if (!resource.isReadable())
            throw new FileProcessException(USER_FILE_NOT_READABLE, userId, filename);
        return resource;
    }

    public void syncingUserFolders() {
        Path folder = getBaseFolder();
        try (Stream<Path> pathStream = Files.walk(folder, 1)) {
            pathStream
                    .parallel()
                    .filter(path -> !path.equals(folder))
                    .forEach(directory -> {
                        try {
                            String filename = directory.getFileName().toString();
                            if (!Files.isDirectory(directory)) {
                                Files.deleteIfExists(directory);
                                log.warn(FILE_REMOVED.getFormattedMessage(filename));
                            }
                            if (!userService.checkUserExists(filename)) {
                                Files.deleteIfExists(directory);
                                log.warn(FILE_REMOVED.getFormattedMessage(filename));
                            }
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

}
