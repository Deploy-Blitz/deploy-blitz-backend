package com.deployblitz.backend.services;

import com.deployblitz.backend.domain.dto.request.GitDeployUpdateBranchRequestDto;
import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public interface GitDeployServices {
    HttpResponse<?> updateBranch(GitDeployUpdateBranchRequestDto gitDeployUpdateBranchRequestDto) throws IOException, GitAPIException;
}
