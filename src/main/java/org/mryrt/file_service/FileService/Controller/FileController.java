package org.mryrt.file_service.FileService.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    // Путь, где будут сохраняться файлы
    private static final String UPLOAD_DIR = "D:\\ITMO\\ПРОЕКТЫ\\file-service\\src\\main\\java\\org\\mryrt\\file_service\\Uploads\\";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Проверка на пустой файл
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Файл не должен быть пустым");
        }

        // Создание директории, если она не существует
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Путь для сохранения файла
        File destFile = new File(directory, file.getOriginalFilename());

        try {
            // Сохранение файла
            file.transferTo(destFile);
            return ResponseEntity.ok("Файл успешно загружен: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке файла");
        }
    }
}
