package org.mryrt.file_service.FileService.Model;

// Jakarta & JPA
import jakarta.persistence.*;

// Lombok annotations
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    private int ownerId;

    private String name;

    private long size;

    private String uuid;

    private String extension;

    @CreatedDate
    private Instant createAt;

    private Instant updateAt;
}

