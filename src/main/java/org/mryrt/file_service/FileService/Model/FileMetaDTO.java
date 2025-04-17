package org.mryrt.file_service.FileService.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
public class FileMetaDTO {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean deletedFromDisk;

    private int id;

    private long ownerId;

    private String name;

    private long size;

    private String uuid;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean suspiciousModified;
    private Instant createAt;
    private Instant updateAt;

    public FileMetaDTO(FileMeta fileMeta) {
        this.id = fileMeta.getId();
        this.name = fileMeta.getName();
        this.ownerId = fileMeta.getOwnerId();
        this.size = fileMeta.getSize();
        this.uuid = fileMeta.getUuid();
        this.deletedFromDisk = fileMeta.isDeletedFromDisk();
        this.suspiciousModified = fileMeta.isSuspiciousModified();
        this.createAt = fileMeta.getCreateAt();
        this.updateAt = fileMeta.getUpdateAt();
    }

}
