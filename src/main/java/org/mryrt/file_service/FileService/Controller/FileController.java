package org.mryrt.file_service.FileService.Controller;

import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("api/files")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping()
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetaDTO fileMetaDTO = fileService.uploadFile(file);
        return ResponseEntity.ok(fileMetaDTO);
    }

    @GetMapping()
    public ResponseEntity getFiles() {
        List<FileMetaDTO> fileMetaList = fileService.getFiles();
        return ResponseEntity.ok(fileMetaList);
    }

}
