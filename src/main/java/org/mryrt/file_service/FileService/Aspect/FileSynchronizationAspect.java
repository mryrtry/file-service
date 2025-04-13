package org.mryrt.file_service.FileService.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Annotation.FileSynchronization;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.mryrt.file_service.FileService.Service.FilePathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_NOT_FOUND_ON_DISK;

@Aspect
@Component
@Slf4j
public class FileSynchronizationAspect {

    @Autowired
    FileMetaRepository fileMetaRepository;

    @Autowired
    FilePathService filePathService;

    @Autowired
    UserService userService;

    @Before("@annotation(ignoredFileSynchronization)")
    public void setFileSynchronization(JoinPoint ignoredJoinPoint, FileSynchronization ignoredFileSynchronization) {
        UserDTO user = userService.getAuthUser();
        List<FileMeta> userFilesMeta = fileMetaRepository.findAllByOwnerId(user.getId());
        List<String> matchedFilenameList = filePathService.synchronizeUserFiles(getDiskFilenames(userFilesMeta), user.getUsername());
        userFilesMeta.forEach(fileMeta -> {
                    if (!matchedFilenameList.contains(fileMeta.getUuid() + fileMeta.getExtension())) {
                        fileMetaRepository.delete(fileMeta);
                        log.warn(FILE_NOT_FOUND_ON_DISK.getFormattedMessage(fileMeta.getName()));
                    }
                }
        );
    }

    private List<String> getDiskFilenames(List<FileMeta> userFilesMeta) {
        return userFilesMeta.stream()
                .map(file -> file.getUuid() + file.getExtension())
                .toList();
    }

}
