package com.deployblitz.backend.repository;

import com.deployblitz.backend.domain.entities.DeployEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeployRepository extends JpaRepository<DeployEntity, Long> {

    DeployEntity findByAppName(String appName);
}
