package org.mryrt.file_service.FileService.Repository;

import org.mryrt.file_service.FileService.Model.FileMeta;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с метаданными файлов.
 *
 * <p>Интерфейс {@link FileMetaRepository} расширяет {@link JpaRepository} и предоставляет методы
 * для доступа к метаданным файлов, включая поиск по UUID и идентификатору владельца.</p>
 *
 * <p>Используется для выполнения операций CRUD и работы с базой данных, связанной с
 * сущностью {@link FileMeta}.</p>
 */
@Repository
public interface FileMetaRepository extends JpaRepository<FileMeta, Integer> {

    /**
     * Ищет метаданные файла по UUID и идентификатору владельца.
     *
     * @param uuid уникальный идентификатор файла
     * @param ownerId идентификатор владельца
     * @return {@link Optional} с {@link FileMeta}, если файл найден, или {@link Optional#empty()} в противном случае
     */
    Optional<FileMeta> findByUuidAndOwnerId(String uuid, long ownerId);

    /**
     * Ищет все метаданные файлов по идентификатору владельца.
     *
     * @param ownerId - идентификатор владельца файла.
     * @return {@link List<FileMeta>}, список всех метаданных найденных файлов.
     */
    List<FileMeta> findAllByOwnerId(long ownerId);
}
