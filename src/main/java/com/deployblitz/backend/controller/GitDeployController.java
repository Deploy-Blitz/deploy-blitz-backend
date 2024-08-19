package com.deployblitz.backend.controller;

import com.deployblitz.backend.domain.dto.request.GitDeployUpdateBranchRequestDto;
import com.deployblitz.backend.services.GitDeployServices;
import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController(value = "/git/deploy")
public class GitDeployController {

    private final GitDeployServices gitDeployServices;

    public GitDeployController(GitDeployServices gitDeployServices) {
        this.gitDeployServices = gitDeployServices;
    }

    @PutMapping("/update/branch")
    public HttpResponse<?> updateBranch(GitDeployUpdateBranchRequestDto gitDeployUpdateBranchRequestDto) throws GitAPIException, IOException {
        return gitDeployServices.updateBranch(gitDeployUpdateBranchRequestDto);
    }

}
