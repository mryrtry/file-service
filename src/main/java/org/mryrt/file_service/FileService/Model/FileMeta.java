package org.mryrt.file_service.FileService.Model;

import jakarta.persistence.*;

// Lombok annotations
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;

// Java time
import java.time.Instant;

@Entity
@Table(name = "file_meta")
@Data
@NoArgsConstructor
@ToString
public class FileMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long ownerId;

    private String name;

    private long size;

    @NaturalId
    private String uuid;

    private String extension;

    @CreatedDate
    private Instant createAt;

    private Instant updateAt;
}

