package com.deployblitz.backend.controller;

import com.deployblitz.backend.domain.dto.request.WebHookCreateRequestDto;
import com.deployblitz.backend.domain.dto.response.WebhookCreateResponseDto;
import com.deployblitz.backend.services.DeployerManager;
import com.deployblitz.backend.utils.HttpResponse;
import jakarta.validation.Valid;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "${deploy-blitz-webhook-endpoint}")
public class WebhookController {

    private final DeployerManager deployerManager;

    public WebhookController(DeployerManager deployerManager) {
        this.deployerManager = deployerManager;
    }

    @PostMapping("/create")
    public HttpResponse<WebhookCreateResponseDto> create(@Valid @RequestBody WebHookCreateRequestDto webhook) throws GitAPIException, TransportException {
        return deployerManager.createWebhook(webhook);
    }

    @PostMapping("/{appName}")
    public HttpResponse<?> invokeWebhook(@PathVariable String appName) throws GitAPIException, IOException {
        return deployerManager.invokeWebhook(appName);
    }

}
