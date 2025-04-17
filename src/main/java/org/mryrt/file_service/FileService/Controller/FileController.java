package org.mryrt.file_service.FileService.Controller;

import lombok.AllArgsConstructor;
import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/files")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping()
    public ResponseEntity<FileMetaDTO> uploadFile(@RequestParam("file") MultipartFile[] files) {
        FileMetaDTO fileMetaDTO = fileService.uploadFile(files);
        return ResponseEntity.ok(fileMetaDTO);
    }

    @GetMapping()
    public ResponseEntity<List<FileMetaDTO>> getFiles() {
        List<FileMetaDTO> fileMetaList = fileService.getFiles();
        return ResponseEntity.ok(fileMetaList);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> getFileByUuid(@PathVariable String uuid) {
        Pair<Resource, HttpHeaders> file = fileService.getFile(uuid);
        return ResponseEntity.ok()
                .headers(file.getSecond())
                .body(file.getFirst());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FileMetaDTO> deleteFileByUuid(@PathVariable String uuid) {
        FileMetaDTO fileMetaDTO = fileService.deleteFile(uuid);
        return ResponseEntity.ok(fileMetaDTO);
    }

}

