package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.services.GitService;
import com.deployblitz.backend.services.ScriptLocator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class GitServiceImpl implements GitService {

    private final ScriptLocator scriptLocator;

    @Value("${deploy-blitz-store-git-repo-path}")
    private String gitRepoPath;

    public GitServiceImpl(ScriptLocator scriptLocator) {
        this.scriptLocator = scriptLocator;
    }

    @Override
    public String cloneGitRepo(String repoUrl, String branch, String token) throws GitAPIException, IOException {

        log.info("Cloning from {} to {} into base path {}", repoUrl, branch, gitRepoPath);

        var cloneUrl = gitRepoPath + "/repo-" + UUID.randomUUID().toString();

        Git.cloneRepository()
                .setURI(repoUrl)
                .setBranch(branch)
                .setCredentialsProvider(returnCredentials(token))
                .setDirectory(createFile(cloneUrl))
                .call();

        log.info("Cloning from {} to {} complete", repoUrl, cloneUrl);

        if(!scriptLocator.repositoryHasScript(cloneUrl)){
            throw new TransportException("Repository not containing script '.deploy-blitz/blitz.sh'");
        }

        return cloneUrl;
    }

    @Override
    public void repoExists(String repoUrl, String branch, String token) throws GitAPIException, TransportException {
        var ref = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(repoUrl)
                .setCredentialsProvider(returnCredentials(token))
                .call()
                .stream()
                .filter(r -> r
                        .getName()
                        .contains(branch))
                .count();

        if (ref == 0) {
            log.info("Repo: {} with branch: {} does not exist", repoUrl, branch);
            throw new TransportException("Repo: " + repoUrl + " with branch: " + branch + " does not exist");
        }

        log.info("ref {}", ref);
    }

    @Override
    public String pullGitRepo(String repoFilePath, String branch, String token) throws GitAPIException, IOException {
        var git = Git.open(getFile(repoFilePath));
        var pullCmd = git.pull().setCredentialsProvider(returnCredentials(token));
        var result = pullCmd.call();

        if (result.isSuccessful()) {
            log.info("Pulling from {} to {}", repoFilePath, branch);
            return result.getFetchResult().getAdvertisedRefs().stream().toList().getFirst().getObjectId().getName();
        }

        throw new TransportException("Pull failed for " + git.getRepository());
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

    private static File getFile(String gitRepoPath) throws IOException {
        var path = Paths.get(gitRepoPath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File or Directory not found" + gitRepoPath);
        }
        return path.toFile();
    }

    private static UsernamePasswordCredentialsProvider returnCredentials(String token) {
        return new UsernamePasswordCredentialsProvider(token, "");
    }
}
