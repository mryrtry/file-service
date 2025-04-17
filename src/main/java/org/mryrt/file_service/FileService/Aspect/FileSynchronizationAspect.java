package org.mryrt.file_service.FileService.Aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Annotation.FileSync;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.mryrt.file_service.FileService.Service.FilePathService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_NOT_FOUND_ON_DISK;

@Aspect
@Component
public class FileSynchronizationAspect {

    private final FileMetaRepository fileMetaRepository;

    private final FilePathService filePathService;

    private final UserService userService;

    public FileSynchronizationAspect(FileMetaRepository fileMetaRepository, FilePathService filePathService, UserService userService) {
        this.fileMetaRepository = fileMetaRepository;
        this.filePathService = filePathService;
        this.userService = userService;
    }

    @Before("@annotation(ignoredFileSync)")
    public void setFileSynchronization(FileSync ignoredFileSync) {
        long userId = userService.getAuthUserId();
        List<FileMeta> userFilesMeta = fileMetaRepository.findAllByOwnerId(userId);
        List<String> userFilenames = userFilesMeta.stream().map(fileMeta -> fileMeta.getUuid() + fileMeta.getExtension()).toList();
        List<String> matchedFilenameList = filePathService.syncingUserFiles(userFilenames, userId);
        userFilesMeta.forEach(fileMeta -> {
            if (!matchedFilenameList.contains(fileMeta.getDiskName())) {
                fileMetaRepository.delete(fileMeta);
                FILE_NOT_FOUND_ON_DISK.log(fileMeta.getName(), userId);
            }
        });
    }

}
