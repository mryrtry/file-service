package org.mryrt.file_service.FileService.Model;

// Lombok annotations
import lombok.Data;

// Java time
import java.time.Instant;

@Data
public class FileMetaDTO {
    public FileMetaDTO(FileMeta fileMeta) {
        this.id = fileMeta.getId();
        this.name = fileMeta.getName();
        this.ownerId = fileMeta.getOwnerId();
        this.size = fileMeta.getSize();
        this.uuid = fileMeta.getUuid();
        this.createAt = fileMeta.getCreateAt();
        this.updateAt = fileMeta.getUpdateAt();
    }

    private int id;

    private int ownerId;

    private String name;

    private long size;

    private String uuid;

    private Instant createAt;

    private Instant updateAt;
}
