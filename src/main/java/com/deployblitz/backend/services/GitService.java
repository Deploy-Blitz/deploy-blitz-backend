package com.deployblitz.backend.services;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;

import java.io.IOException;

public interface GitService {
    String cloneGitRepo(String repoUrl, String branch, String token) throws GitAPIException, IOException;

    void repoExists(String repoUrl, String branch, String token) throws GitAPIException, TransportException;

    String pullGitRepo(String repoFilePath, String branch, String token) throws GitAPIException, IOException;
}
