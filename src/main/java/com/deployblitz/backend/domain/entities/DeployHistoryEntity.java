package com.deployblitz.backend.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity(name = "deploy_history")
public class DeployHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deploy_id", nullable = false, unique = true)
    private DeployEntity deploy;

    @Column(name = "endpoint_path", nullable = false, length = 255)
    private String endpointPath;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String script;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}
