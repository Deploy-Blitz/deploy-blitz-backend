package com.deployblitz.backend.domain.entities;

import com.deployblitz.backend.domain.dto.request.WebHookCreateRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity(name = "deploy")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "endpoint_path_idx", columnList = "endpoint_path"),
        @Index(name = "git_dir_path_idx", columnList = "git_directory_path")
})
public class DeployEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false, unique = true)
    private String appName;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "endpoint_path", nullable = false)
    private String endpointPath;

    @Column(name = "git_token")
    private String gitToken;

    @Column(name = "git_directory_path")
    private String gitDirectoryPath;

    @Column(name = "git_branch", columnDefinition = "varchar(100) not null default 'main'")
    private String gitBranch;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @JsonIgnore
    @OneToMany(mappedBy = "deploy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeployHistoryEntity> deployHistories;

    public DeployEntity(WebHookCreateRequestDto dto) {
        this.appName = dto.name();
        this.endpointPath = dto.gitHttpsUri();
        this.gitToken = dto.gitToken();
        this.gitBranch = dto.branch();
        this.creationDate = LocalDateTime.now();
    }
}
