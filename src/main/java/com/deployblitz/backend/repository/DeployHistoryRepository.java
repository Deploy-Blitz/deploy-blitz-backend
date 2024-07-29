package com.deployblitz.backend.repository;

import com.deployblitz.backend.domain.entities.DeployHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeployHistoryRepository extends JpaRepository<DeployHistoryEntity, Long> {
}
