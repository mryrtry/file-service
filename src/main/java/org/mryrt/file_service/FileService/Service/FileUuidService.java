package org.mryrt.file_service.FileService.Service;

// Pair
import org.springframework.data.util.Pair;

// Java time
import java.time.Instant;

/**
 * Интерфейс для генерации и декодирования уникальных идентификаторов файлов.
 */
public interface FileUuidService {
    /**
     * Генерирует уникальный идентификатор (UUID) для указанного файла.
     *
     * @param fileName  имя файла, для которого необходимо сгенерировать UUID.
     * @param createDate дата создания файла.
     * @return сгенерированный уникальный идентификатор.
     */
    String generateUuid(String fileName, Instant createDate);
}
