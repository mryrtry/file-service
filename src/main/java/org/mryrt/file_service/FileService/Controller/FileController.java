package org.mryrt.file_service.FileService.Controller;

import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

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

    @GetMapping("/get/all")
    public ResponseEntity getFileAll() {
        List<FileMetaDTO> fileList = fileService.getAllFiles();
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/get/startsWith/{startsWith}")
    public ResponseEntity getFileStarts(@PathVariable String startsWith) {
        List<FileMetaDTO> fileList = fileService.getAllFiles(file -> file.getName().startsWith(startsWith));
        return ResponseEntity.ok(fileList);
    }

    @DeleteMapping("/delete/uuid/{uuid}")
    public ResponseEntity deleteFileUuid(@PathVariable String uuid) {
        try {
            FileMetaDTO file = fileService.deleteFile(uuid);
            return ResponseEntity.ok(file);
        } catch (FileNotFoundException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("delete/all")
    public ResponseEntity deleteFileAll() {
        List<FileMetaDTO> fileList = fileService.deleteAllFiles();
        return ResponseEntity.ok(fileList);
    }

    @DeleteMapping("delete/all/startsWith/{startsWith}")
    public ResponseEntity deleteFileStarts(@PathVariable String startsWith) {
        List<FileMetaDTO> files = fileService.deleteAllFiles(file -> file.getName().startsWith(startsWith));
        return ResponseEntity.ok(files);
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
