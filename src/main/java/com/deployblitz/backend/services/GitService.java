package com.deployblitz.backend.services;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public interface GitService {
    String cloneGitRepo(String repoUrl, String branch, String token) throws GitAPIException, IOException;
}
