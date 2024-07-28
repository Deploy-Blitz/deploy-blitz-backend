package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.services.GitService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    @Value("${deploy-blitz-store-git-repo-path}")
    private String gitRepoPath;

    @Override
    public String cloneGitRepo(String repoUrl, String branch, String token) throws GitAPIException, IOException {

        log.info("Cloning from {} to {} into base path {}", repoUrl, branch, gitRepoPath);

        var cloneUrl = gitRepoPath + "/repo-" + UUID.randomUUID().toString();

        Git.cloneRepository()
                .setURI(repoUrl)
                .setBranch(branch)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(createFile(cloneUrl))
                .call();

        log.info("Cloning from {} to {} complete", repoUrl, cloneUrl);
        return cloneUrl;
    }

    private File createFile(String cloneUrl) throws IOException {
        var baseDir = new File(gitRepoPath);
        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                throw new IOException("Failed to create base directory: " + gitRepoPath);
            }
        }

        var cloneDir = new File(cloneUrl);
        if (cloneDir.exists()) {
            throw new IOException("Clone directory already exists: " + cloneUrl);
        }

        return cloneDir;
    }
}
