package org.mryrt.file_service.FileService.Service;

import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Exceptions.FileProcessException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.file.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;
import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.*;

@Service
@TrackExecutionTime
public class FilePathService {

    private final ResourceLoader resourceLoader;
    private final UserService userService;
    @Value("${file.service.upload-dir}")
    private String UPLOAD_DIR;

    public FilePathService(ResourceLoader resourceLoader, UserService userService) {
        this.resourceLoader = resourceLoader;
        this.userService = userService;
    }

    private Path getUserFolder(long userId) {
        try {
            Path folder = Paths.get(UPLOAD_DIR).resolve(String.valueOf(userId)).toAbsolutePath().normalize();
            if (!Files.isDirectory(folder)) {
                Files.createDirectories(folder);
            } else if (!Files.isReadable(folder)) {
                throw new FileProcessException(FilesErrorMessage.USER_DIRECTORY_NOT_READABLE, userId);
            }
            return folder;
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        }
    }

    private Path getBaseFolder() {
        Path folder = Paths.get(UPLOAD_DIR).normalize();
        try {
            if (Files.isDirectory(folder)) Files.createDirectories(folder);
        } catch (IOException ignored) {
        }
        return folder;
    }

    public long getUserFolderSize(long userId) {
        Path folder = getUserFolder(userId);
        LongAdder folderSize = new LongAdder();

        try (Stream<Path> pathStream = Files.walk(folder)) {
            pathStream.parallel().filter(Files::isRegularFile).forEach(file -> {
                try {
                    folderSize.add(Files.size(file));
                } catch (IOException exception) {
                    FILE_SKIPPED.log(file.getFileName(), exception.getMessage());
                }
            });
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        } finally {
            Cleaner.create().register(this, System::gc);
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
                        String filename = file.getFileName().toString();
                        try {
                            if (!filenameList.contains(filename)) {
                                Files.deleteIfExists(file);
                                NOT_USER_FILE.log(filename, userId);
                                return;
                            }
                            if (Files.isDirectory(file)) {
                                Files.deleteIfExists(file);
                                FILE_IS_DIRECTORY.log(filename, userId);
                                return;
                            }
                            if (!Files.isReadable(file)) {
                                Files.deleteIfExists(file);
                                FILE_NOT_READABLE.log(filename, userId);
                                return;
                            }
                            existingFiles.add(filename);
                        } catch (IOException exception) {
                            FILE_SKIPPED.log(filename, exception.getMessage());
                        }
                    });
        } catch (IOException exception) {
            throw new FileProcessException(USER_DIRECTORY_ACCESS_ERROR, exception, userId);
        } finally {
            Cleaner.create().register(this, System::gc);
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
                                BASE_DIRECTORY_INVALID_FILE_REMOVED.log(filename);
                            }
                            if (!userService.checkUserExists(filename)) {
                                Files.deleteIfExists(directory);
                                NONEXISTENT_USER_DIRECTORY_REMOVED.log(filename);
                            }
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    public List<FileMeta> checkUserFilesWasSuspiciousModified(List<FileMeta> fileMetas, long userId) {
        Path folder = getUserFolder(userId);
        return fileMetas.stream().peek(fileMeta -> {
            try {
                Path file = folder.resolve(fileMeta.getDiskName()).normalize();
                if (Duration.between(fileMeta.getUpdateAt(), Files.getLastModifiedTime(file).toInstant()).toMillis() > 1000)
                    fileMeta.setSuspiciousModified(true);
            } catch (IOException ignored) {
                FILE_NOT_READABLE.log(fileMeta.getName(), String.valueOf(userId));
            }
        }).toList();
    }

}
