package org.mryrt.file_service.FileService.Model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "file_meta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FileMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String mimetype;

    @Column(nullable = false)
    private String extension;

    @NaturalId
    @Column(unique = true, nullable = false)
    private String uuid;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private Instant createAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updateAt;
}