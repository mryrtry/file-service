package org.mryrt.file_service.FileService.Service;

import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.FileService.Exceptions.FileSizeExceededException;
import org.mryrt.file_service.FileService.Exceptions.NotEnoughSpaceException;
import org.mryrt.file_service.FileService.Model.FileMeta;
import org.mryrt.file_service.FileService.Model.FileMetaDTO;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// Spring web
import org.springframework.web.multipart.MultipartFile;

// Java IO
import java.io.File;
import java.io.FileNotFoundException;

// Java util
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Lombok logger
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    // TODO: вынести дублирующийся код в приватные методы

    @Autowired
    FileMetaRepository fileMetaRepository;

    @Autowired
    UserService userService;

    @Autowired
    FileUuidService uuidService;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Подгружаемая из конфигурации директория для загрузки файлов.
     */
    @Value("${file.service.upload-dir}")
    private String UPLOAD_DIR;

    /**
     * Подгружаемый из документации максимальный размер загружаемого файла.
     */
    @Value("${file.service.max-file-size}")
    private int MAX_FILE_SIZE;

    /**
     * Подгружаемый из документации максимальный размер папки пользователя.
     */
    @Value("${file.service.max-folder-size}")
    private long MAX_FOLDER_SIZE;


    /**
     * Получает расширение файла из объекта {@link MultipartFile}.
     *
     * <p>Метод извлекает расширение файла, основываясь на оригинальном имени файла,
     * возвращаемом методом {@link MultipartFile#getOriginalFilename()}. Если имя файла
     * не содержит точки или является {@code null}, метод возвращает {@code null}.</p>
     *
     * @param file объект {@link MultipartFile}, из которого нужно получить расширение
     * @return строку, представляющую расширение файла, или {@code null}, если расширение отсутствует
     */
    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "";
    }

    /**
     * Проверяет, что размер загруженного файла не превышает установленный предел.
     *
     * @param file загруженный файл, который необходимо проверить.
     * @throws FileSizeExceededException если размер файла превышает максимально допустимый размер.
     */
    private void assertFileSize(MultipartFile file) throws FileSizeExceededException {
        log.debug("Checking file '{}' size in limits", file.getOriginalFilename());
        if (file.getSize() > MAX_FILE_SIZE) throw new FileSizeExceededException();
    }

    /**
     * Проверяет, существует ли директория пользователя, и создаёт её, если она отсутствует.
     *
     * <p>
     * Метод использует имя пользователя для формирования пути к директории в указанной
     * корневой директории загрузки (UPLOAD_DIR). Если директория не существует,
     * она будет создана.
     * </p>
     *
     * @param user объект {@link UserDTO}, содержащий информацию о пользователе,
     *             в частности, имя пользователя, используемое для создания директории.
     */
    private File assertUserDirectory(UserDTO user) {
        log.debug("Checking user '{}' directory exists", user.getUsername());
        File directory = new File(UPLOAD_DIR + "/" + user.getUsername());
        if (!directory.exists() && directory.mkdir())
            log.info("User  {} directory created", user.getUsername());
        return directory;
    }

    /**
     * Получает метаданные файла по UUID для указанного пользователя.
     *
     * <p>Если метаданные файла не найдены, выбрасывается исключение {@link FileNotFoundException}.</p>
     *
     * @param user объект {@link UserDTO}, представляющий пользователя, которому принадлежит файл
     * @param uuid уникальный идентификатор файла
     * @return объект {@link FileMeta}, представляющий метаданные файла
     * @throws FileNotFoundException если файл с указанным UUID не найден в репозитории пользователя
     */
    private FileMeta inGetFileMeta(UserDTO user, String uuid) throws FileNotFoundException {
        log.info("Getting uuid {} meta in user {} repository", uuid, user.getUsername());
        return fileMetaRepository.findByUuidAndOwnerId(uuid, user.getId())
                .orElseThrow(() -> {
                    log.warn("File uuid {} not found in user {} repository", uuid, user.getUsername());
                    return new FileNotFoundException();
                });
    }

    // TODO: docs
    private void inDeleteFile(String username, FileMeta fileMeta, File directory) throws FileNotFoundException {
        String fileName = fileMeta.getUuid() + fileMeta.getExtension();
        log.debug("Checking if file {} exists in user {} repository", fileName, username);
        File file = new File(directory, fileName);
        if (!file.exists()) {
            log.warn("Could not find file {} in user {} repository", fileName, username);
            throw new FileNotFoundException("File not found");
        }
        if (file.delete()) {
            log.info("File {} deleted successfully from user {} repository", fileName, username);
            fileMetaRepository.delete(fileMeta);
        } else {
            log.warn("Could not delete file {} from user {} repository", fileName, username);
            throw new FileNotFoundException("Could not delete file");
        }
    }

    private List<FileMetaDTO> inDeleteAll(UserDTO user, File directory, List<FileMeta> files) {
        List<FileMeta> notFoundFiles = new ArrayList<>();
        files.forEach(file -> {
            try {
                inDeleteFile(user.getUsername(), file, directory);
            } catch (FileNotFoundException exception) {
                notFoundFiles.add(file);
            }
        });
        if (!notFoundFiles.isEmpty()) {
            files.removeAll(notFoundFiles);
            fileMetaRepository.deleteAll(notFoundFiles);
        }
        return files.stream().map(FileMetaDTO::new).toList();
    }

    // todo: если File last modified не совпал с file meta last modified - кидать алёрт
    private void deleteInvalidFiles(UserDTO user, File directory, List<FileMeta> files) {
        List<FileMeta> invalidFiles = new ArrayList<>();
        files.forEach(file -> {
            File invalidFile = new File(directory, file.getUuid() + file.getExtension());
            if (!invalidFile.exists()) invalidFiles.add(file);
        });
        if (!invalidFiles.isEmpty()) {
            log.warn("Deleted invalid files {} from user {} directory", invalidFiles, user.getUsername());
            fileMetaRepository.deleteAll(invalidFiles);
            files.removeAll(invalidFiles);
        }
    }

    /**
     * Обрабатывает метаданные файла и создает объект {@link FileMeta}.
     *
     * <p>
     * Метод принимает объект {@link UserDTO} и загруженный файл в формате
     * {@link MultipartFile}. Он извлекает информацию о файле, такой как имя,
     * размер и создает уникальный идентификатор. Метаданные файла также
     * включают идентификатор владельца, дату создания и дату последнего
     * обновления.
     * </p>
     *
     * @param user объект {@link UserDTO}, представляющий пользователя,
     *             которому принадлежит файл.
     * @param file загруженный файл в формате {@link MultipartFile},
     *             для которого необходимо создать метаданные.
     * @return объект {@link FileMeta}, содержащий метаданные загруженного файла.
     */
    private FileMeta processFileMeta(UserDTO user, MultipartFile file) {
        log.debug("Processing file meta for user {} file {}", user.getUsername(), file.getOriginalFilename());
        FileMeta fileMeta = new FileMeta();
        fileMeta.setOwnerId(user.getId());
        fileMeta.setName(file.getOriginalFilename());
        fileMeta.setExtension(getFileExtension(file));
        fileMeta.setSize(file.getSize());
        fileMeta.setCreateAt(Instant.now());
        fileMeta.setUpdateAt(Instant.now());
        fileMeta.setUuid(uuidService.generateUuid(fileMeta.getName(), fileMeta.getCreateAt()));
        log.info("User {} file {} processed", user.getUsername(), file.getOriginalFilename());
        return fileMeta;
    }

    @Override
    public FileMetaDTO uploadFile(MultipartFile file) throws FileSizeExceededException, NotEnoughSpaceException {
        assertFileSize(file);
        UserDTO user = userService.getAuthUser();
        FileMeta fileMeta = processFileMeta(user, file);
        File directory = assertUserDirectory(user);
        try {
            File uploadFile = new File(directory, fileMeta.getUuid() + fileMeta.getExtension());
            log.debug("Transferring file {} to disk", file.getOriginalFilename());
            file.transferTo(uploadFile);
        } catch (IOException exception) {
            log.warn("Not enough space on disk or other problem with transferring file {}", file.getOriginalFilename());
            throw new NotEnoughSpaceException();
        }
        fileMetaRepository.save(fileMeta);
        log.info("File {} successfully saved to disk by user {}", file.getOriginalFilename(), user.getUsername());
        return new FileMetaDTO(fileMeta);
    }

    @Override
    public ResponseEntity getFile(String uuid) throws FileNotFoundException {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        FileMeta fileMeta = inGetFileMeta(user, uuid);
        String fileName = uuid + fileMeta.getExtension();
        Resource resource = resourceLoader.getResource("file:" + directory.getAbsoluteFile() + "/" + fileName);
        if (!resource.exists()) {
            log.debug("File meta found, but file resource doesn't exist, aborting");
            throw new FileNotFoundException("File resource not found");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMeta.getName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        return new ResponseEntity(resource, headers, HttpStatus.OK);
    }

    @Override
    public List<FileMetaDTO> getAllFiles() {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        log.info("Searching for files in user {} repository", user.getUsername());
        List<FileMeta> files = fileMetaRepository.findAllByOwnerId(user.getId());
        deleteInvalidFiles(user, directory, files);
        return files.stream().map(FileMetaDTO::new).toList();
    }

    @Override
    public List<FileMetaDTO> getAllFiles(Predicate<FileMeta> predicate) {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        log.info("Searching for files in user {} repository by predicate", user.getUsername());
        List<FileMeta> files = fileMetaRepository.findAllByOwnerId(user.getId());
        deleteInvalidFiles(user, directory, files);
        return files.stream().filter(predicate).map(FileMetaDTO::new).toList();
    }

    @Override
    public FileMetaDTO deleteFile(String uuid) throws FileNotFoundException {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        FileMeta fileMeta = inGetFileMeta(user, uuid);
        inDeleteFile(user.getUsername(), fileMeta, directory);
        return new FileMetaDTO(fileMeta);
    }

    @Override
    public List<FileMetaDTO> deleteAllFiles() {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        List<FileMeta> files = fileMetaRepository.findAllByOwnerId(user.getId());
        log.debug("Processing fileMeta list: {}", files);
        return inDeleteAll(user, directory, files);
    }

    @Override
    public List<FileMetaDTO> deleteAllFiles(Predicate<FileMeta> predicate) {
        UserDTO user = userService.getAuthUser();
        File directory = assertUserDirectory(user);
        List<FileMeta> files = fileMetaRepository.findAllByOwnerId(user.getId());
        log.debug("Processing fileMeta list: {}", files);
        files = files.stream().filter(predicate).toList();
        return inDeleteAll(user, directory, files);
    }

    @Override
    public FileMetaDTO touchFile(String uuid) throws FileNotFoundException {
        UserDTO user = userService.getAuthUser();
        FileMeta fileMeta = inGetFileMeta(user, uuid);
        fileMeta.setUpdateAt(Instant.now());
        fileMetaRepository.save(fileMeta);
        return new FileMetaDTO(fileMeta);
    }
}
