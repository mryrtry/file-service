package org.mryrt.file_service.FileService.Service;

import lombok.AllArgsConstructor;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@TrackExecutionTime
@AllArgsConstructor
public class FileMetaService {

    final
    FileMetaRepository fileMetaRepository;

    private static String getBaseName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }

    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    public FileMeta getFileMeta(long userId, MultipartFile file) {
        String filename = getFilename(userId, file.getOriginalFilename());
        return FileMeta.builder()
                .ownerId(userId)
                .name(filename)
                .uuid(getUuid(userId, filename))
                .size(file.getSize())
                .extension(getExtension(filename))
                .build();
    }

    private String getFilename(long userId, String filename) {
        String receivedBaseName = getBaseName(filename);
        String fileBaseName = receivedBaseName;
        List<String> filenameList = fileMetaRepository.findAllByOwnerId(userId)
                .stream()
                .map(FileMeta::getName)
                .map(FileMetaService::getBaseName)
                .toList();

        int counter = 0;
        while (filenameList.contains(fileBaseName))
            fileBaseName = "%s(%d)".formatted(receivedBaseName, ++counter);

        return fileBaseName + getExtension(filename);
    }

    private String getUuid(long userId, String filename) {
        byte[] nameBytes = "%d-%s".formatted(userId, filename).getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(nameBytes).toString();
    }

}
