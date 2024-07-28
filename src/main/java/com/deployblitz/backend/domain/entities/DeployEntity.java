package com.deployblitz.backend.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity(name = "deploy")
public class DeployEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false, unique = true, length = 255)
    private String appName;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "endpoint_path", nullable = false, length = 255)
    private String endpointPath;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String script;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "deploy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeployHistoryEntity> deployHistories;
}
