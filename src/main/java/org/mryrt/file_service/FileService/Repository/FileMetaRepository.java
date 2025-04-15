package org.mryrt.file_service.FileService.Repository;

import org.mryrt.file_service.FileService.Model.FileMeta;

import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@TrackExecutionTime
public interface FileMetaRepository extends JpaRepository<FileMeta, Integer> {

    Optional<FileMeta> findByUuidAndOwnerId(String uuid, long ownerId);

    List<FileMeta> findAllByOwnerId(long ownerId);

}
