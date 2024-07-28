package com.deployblitz.backend.controller;

import com.deployblitz.backend.domain.dto.request.WebookCreateRequestDto;
import com.deployblitz.backend.services.GitService;
import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(name = "/webhook")
public class WebhookController {

    private final GitService gitService;

    public WebhookController(GitService gitService) {
        this.gitService = gitService;
    }

    @PostMapping("/create")
    public HttpResponse<?> create(@RequestBody WebookCreateRequestDto webhook) throws GitAPIException, IOException {
        //ToDo: Miss Logic for handle services
        return new HttpResponse<Object>(HttpStatus.OK, gitService.cloneGitRepo(webhook.gitHttpsUri(), webhook.branch(), webhook.gitToken()));
    }

}
