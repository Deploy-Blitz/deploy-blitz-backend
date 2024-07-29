package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.domain.dto.request.WebHookCreateRequestDto;
import com.deployblitz.backend.domain.dto.response.WebhookCreateResponseDto;
import com.deployblitz.backend.domain.entities.DeployEntity;
import com.deployblitz.backend.domain.entities.DeployHistoryEntity;
import com.deployblitz.backend.repository.DeployHistoryRepository;
import com.deployblitz.backend.repository.DeployRepository;
import com.deployblitz.backend.services.DeployerManager;
import com.deployblitz.backend.services.GitService;
import com.deployblitz.backend.services.ScriptLocator;
import com.deployblitz.backend.utils.HttpResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
public class DeployerManagerImpl implements DeployerManager {

    private final DeployRepository deployRepository;

    private final DeployHistoryRepository deployHistoryRepository;

    private final GitService gitService;

    private final ScriptLocator scriptLocator;

    public DeployerManagerImpl(DeployRepository deployRepository, DeployHistoryRepository deployHistoryRepository, GitService gitService, ScriptLocator scriptLocator) {
        this.deployRepository = deployRepository;
        this.deployHistoryRepository = deployHistoryRepository;
        this.gitService = gitService;
        this.scriptLocator = scriptLocator;
    }

    @Transactional
    @Override
    public HttpResponse<WebhookCreateResponseDto> createWebhook(WebHookCreateRequestDto dto) throws GitAPIException, TransportException {
        gitService.repoExists(dto.gitHttpsUri(), dto.branch(), dto.gitToken());
        var result = deployRepository.save(new DeployEntity(dto));
        return new HttpResponse<>(OK, new WebhookCreateResponseDto(result.getAppName()));
    }

    @Override
    public HttpResponse<?> invokeWebhook(String name) throws GitAPIException, IOException {
        var isNewRepo = false;
        final var result = deployRepository.findByAppName(name);
        if (result == null) return new HttpResponse<>(NOT_FOUND);

        if (result.getGitDirectoryPath() == null) {
            isNewRepo = true;
            var repoPath = gitService.cloneGitRepo(result.getEndpointPath(), result.getGitBranch(), result.getGitToken());
            result.setGitDirectoryPath(repoPath);
            deployRepository.save(result);
            var scriptAsString = scriptLocator.getScript(result.getGitDirectoryPath());
            deployHistoryRepository.save(new DeployHistoryEntity(result, "first-invoke", scriptAsString));
        }

        if (!isNewRepo) {
            var commitId = gitService.pullGitRepo(result.getGitDirectoryPath(), result.getGitBranch(), result.getGitToken());
            var scriptAsString = scriptLocator.getScript(result.getGitDirectoryPath());
            log.info("Commit ID {} from {} to {}", commitId, result.getGitDirectoryPath(), result.getGitBranch());
            deployHistoryRepository.save(new DeployHistoryEntity(result, commitId, scriptAsString));
        }

        return new HttpResponse<>(OK, result);
    }
}
