package com.deployblitz.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "deploy_history")
public class DeployHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deploy_id", nullable = false)
    private DeployEntity deploy;

    @Column(nullable = false)
    private String commitId;

    @Column(name = "endpoint_path", nullable = false)
    private String endpointPath;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String script;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    public DeployHistoryEntity(DeployEntity deploy, String commitId, String script) {
        this.deploy = deploy;
        this.commitId = commitId;
        this.endpointPath = deploy.getEndpointPath();
        this.script = script;
    }
}
