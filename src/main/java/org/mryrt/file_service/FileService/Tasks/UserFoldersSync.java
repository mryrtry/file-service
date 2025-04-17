package org.mryrt.file_service.FileService.Tasks;

import org.mryrt.file_service.FileService.Service.FilePathService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "tasks.folder-sync.enabled", havingValue = "true")
public class UserFoldersSync {

    final
    FilePathService filePathService;

    public UserFoldersSync(FilePathService filePathService) {
        this.filePathService = filePathService;
    }

    @Scheduled(fixedRateString = "${tasks.folder-sync.frequency:5000}")
    public void syncFolders() {
        filePathService.syncingUserFolders();
    }

}
