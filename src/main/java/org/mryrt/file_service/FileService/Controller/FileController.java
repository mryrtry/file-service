package org.mryrt.file_service.FileService.Controller;

// Custom file service and FileMetaModel
import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Service.FileService;

// Spring annotations
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

// Spring web
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/files")
public class FileController {

    // TODO: docs и оставшиеся эндпоинты

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileMetaDTO fileMeta = fileService.uploadFile(file);
            return ResponseEntity.ok(fileMeta);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/get/uuid/{uuid}")
    public ResponseEntity getFileUuid(@PathVariable String uuid) {
        try {
            return fileService.getFile(uuid);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/touch/uuid/{uuid}")
    public ResponseEntity touchFileUuid(@PathVariable String uuid) {
        try {
            FileMetaDTO fileMeta = fileService.touchFile(uuid);
            return ResponseEntity.ok(fileMeta);
        } catch (FileNotFoundException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
