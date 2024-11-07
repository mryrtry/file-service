package org.mryrt.file_service.FileService.Service;

// Custom FileMeta (DTO), exceptions
import org.mryrt.file_service.FileService.Exceptions.FileSizeExceededException;
import org.mryrt.file_service.FileService.Exceptions.NotEnoughSpaceException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Model.FileMetaDTO;

// Spring annotations
import org.springframework.stereotype.Service;

// Spring web
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

// Java IO
import java.io.FileNotFoundException;

// Java util
import java.util.List;
import java.util.function.Predicate;

/**
 * Интерфейс для работы с файлами в файловом сервисе.
 * Предоставляет методы для загрузки, получения, удаления и управления файлами.
 */
@Service
public interface FileService {
    /**
     * Загружает файл и сохраняет его метаданные.
     *
     * @param file файл, который необходимо загрузить.
     * @return объект {@link FileMetaDTO}, содержащий метаданные загруженного файла.
     */
    FileMetaDTO uploadFile(MultipartFile file) throws FileSizeExceededException, NotEnoughSpaceException;

    /**
     * Получает метаданные файла по его уникальному идентификатору (UUID).
     *
     * @param uuid уникальный идентификатор файла.
     * @return объект {@link FileMetaDTO}, содержащий метаданные файла.
     */
    ResponseEntity getFile(String uuid) throws FileNotFoundException;

    /**
     * Получает метаданные всех файлов.
     *
     * @return список объектов {@link FileMetaDTO}, содержащих метаданные всех файлов.
     */
    List<FileMetaDTO> getAllFiles();

    /**
     * Получает метаданные всех файлов, удовлетворяющих заданному предикату.
     *
     * @param predicate предикат для фильтрации файлов.
     * @return список объектов {@link FileMetaDTO}, содержащих метаданные файлов, удовлетворяющих предикату.
     */
    List<FileMetaDTO> getAllFiles(Predicate<FileMeta> predicate);

    /**
     * Удаляет файл по его уникальному идентификатору (UUID).
     *
     * @param uuid уникальный идентификатор файла.
     * @return объект {@link FileMetaDTO}, содержащий метаданные удаленного файла.
     */
    FileMetaDTO deleteFile(String uuid) throws FileNotFoundException;

    /**
     * Удаляет все файлы и возвращает метаданные удаленных файлов.
     *
     * @return список объектов {@link FileMetaDTO}, содержащих метаданные всех удаленных файлов.
     */
    List<FileMetaDTO> deleteAllFiles();

    /**
     * Удаляет все файлы, удовлетворяющие заданному предикату.
     *
     * @param predicate предикат для фильтрации файлов, которые будут удалены.
     * @return список объектов {@link FileMetaDTO}, содержащих метаданные удаленных файлов.
     */
    List<FileMetaDTO> deleteAllFiles(Predicate<FileMeta> predicate);

    /**
     * Обновляет метаданные файла по его уникальному идентификатору (UUID).
     *
     * @param uuid уникальный идентификатор файла.
     * @return объект {@link FileMetaDTO}, содержащий обновленные метаданные файла.
     */
    FileMetaDTO touchFile(String uuid) throws FileNotFoundException;
}
