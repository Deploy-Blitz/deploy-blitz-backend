package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.domain.dto.request.GitDeployUpdateBranchRequestDto;
import com.deployblitz.backend.repository.DeployRepository;
import com.deployblitz.backend.services.GitDeployServices;
import com.deployblitz.backend.services.GitService;
import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GitDeployServicesImpl implements GitDeployServices {

    private final DeployRepository deployRepository;

    private final GitService gitService;

    public GitDeployServicesImpl(DeployRepository deployRepository, GitService gitService) {
        this.deployRepository = deployRepository;
        this.gitService = gitService;
    }

    @Override
    public HttpResponse<?> updateBranch(GitDeployUpdateBranchRequestDto gitDeployUpdateBranchRequestDto) throws IOException, GitAPIException {
        var response = deployRepository.findByAppName(gitDeployUpdateBranchRequestDto.appName());
        if (response == null) {
            return new HttpResponse<>(HttpStatus.NOT_FOUND);
        }
        gitService.repoExists(response.getEndpointPath(), gitDeployUpdateBranchRequestDto.branch(), response.getGitToken());
        deployRepository.save(response);
        return new HttpResponse<>(HttpStatus.OK);
    }
}
