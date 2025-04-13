package org.mryrt.file_service.FileService.Parser;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Slf4j
@Component
public class FileMetaParser {

    public FileMeta parse(MultipartFile file) {
        return FileMeta.builder()
                .size(file.getSize())
                .extension(getExtensionFromFilename(file.getOriginalFilename()))
                .build();
    }

    private static String getExtensionFromFilename(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".")))
                .orElse("");
    }

}
